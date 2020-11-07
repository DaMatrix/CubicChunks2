package io.github.opencubicchunks.cubicchunks.mixin.core.client.debug;

import io.github.opencubicchunks.cubicchunks.debug.DebugVisualization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Shadow @Nullable public ClientLevel level;

    @Inject(method = "setLevel", at = @At("HEAD"))
    private void unloadWorld(ClientLevel clientLevel, CallbackInfo ci) {
        if (this.level != null) {
            DebugVisualization.onWorldUnload(this.level);
        }
    }

    @Inject(method = "clearLevel(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"))
    private void unloadWorld(Screen screen, CallbackInfo ci) {
        if (this.level != null) {
            DebugVisualization.onWorldUnload(this.level);
        }
    }
}