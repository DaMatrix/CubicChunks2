package io.github.opencubicchunks.cubicchunks.chunk.cube;

import com.google.common.collect.Maps;
import io.github.opencubicchunks.cubicchunks.chunk.ICube;
import io.github.opencubicchunks.cubicchunks.chunk.util.CubePos;
import io.github.opencubicchunks.cubicchunks.utils.Coords;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.ITickList;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.StructureStart;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class CubePrimer implements ICube, IChunk {

    private final CubePos cubePos;
    private final ChunkSection[] sections;
    private ChunkStatus status = ChunkStatus.EMPTY;

    private final Map<BlockPos, TileEntity> tileEntities = Maps.newHashMap();
    private volatile boolean modified = true;

    //TODO: add TickList<Block> and TickList<Fluid>
    public CubePrimer(CubePos pos, @Nullable ChunkSection[] sectionsIn)
    {
        this.cubePos = pos;
        if(sectionsIn == null) {
            this.sections = new ChunkSection[ICube.CUBESIZE];
            for(int i = 0; i < ICube.CUBESIZE; i++) {
                this.sections[i] = new ChunkSection(pos.getY(), (short) 0, (short) 0, (short) 0);
            }
        }
        else {
            if(sectionsIn.length == ICube.CUBESIZE)
                this.sections = sectionsIn;
            else
            {
                throw new IllegalStateException("Number of Sections must equal Cube.CUBESIZE");
            }
        }
    }

    @Override
    public ChunkSection[] getCubeSections() {
        return this.sections;
    }

    @Nullable @Override public TileEntity getTileEntity(BlockPos pos) {
        throw new UnsupportedOperationException("For later implementation");
    }

    public BlockState getBlockState(int x, int y, int z) {
        int index = Coords.blockToIndex32(x, y, z);
        return ChunkSection.isEmpty(this.sections[index]) ?
                Blocks.AIR.getDefaultState() :
                this.sections[index].getBlockState(x & 15, y & 15, z & 15);
    }

    @Override public IFluidState getFluidState(BlockPos pos) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Nullable
    public BlockState setBlock(BlockPos pos, BlockState state, boolean isMoving) {
        int x = pos.getX() & 0xF;
        int y = pos.getY() & 0xF;
        int z = pos.getZ() & 0xF;
        int index = Coords.blockToIndex32(pos.getX(), pos.getY(), pos.getZ());

        if (this.sections[index] == Chunk.EMPTY_SECTION && state.getBlock() == Blocks.AIR) {
            return state;
        } else {
            return this.sections[index].setBlockState(x, y, z, state);

            //TODO: finish implementing
            /*if (state.getLightValue(this, pos) > 0) {
                this.lightPositions.add(new BlockPos((x & 15) + this.getPos().getXStart(), y, (z & 15) + this.getPos().getZStart()));
            }

            ChunkSection chunksection = this.getSection(y >> 4);
            BlockState blockstate = chunksection.setBlockState(x & 15, y & 15, z & 15, state);
            if (this.status.isAtLeast(ChunkStatus.FEATURES) && state != blockstate && (state.getOpacity(this, pos) != blockstate.getOpacity(this, pos) || state.getLightValue(this, pos) != blockstate.getLightValue(this, pos) || state.isTransparent() || blockstate.isTransparent())) {
                WorldLightManager worldlightmanager = this.getWorldLightManager();
                worldlightmanager.checkBlock(pos);
            }

            EnumSet<Heightmap.Type> enumset1 = this.getStatus().getHeightMaps();
            EnumSet<Heightmap.Type> enumset = null;

            for(Heightmap.Type heightmap$type : enumset1) {
                Heightmap heightmap = this.heightmaps.get(heightmap$type);
                if (heightmap == null) {
                    if (enumset == null) {
                        enumset = EnumSet.noneOf(Heightmap.Type.class);
                    }

                    enumset.add(heightmap$type);
                }
            }

            if (enumset != null) {
                Heightmap.updateChunkHeightmaps(this, enumset);
            }

            for(Heightmap.Type heightmap$type1 : enumset1) {
                this.heightmaps.get(heightmap$type1).update(x & 15, y, z & 15, state);
            }

            return blockstate;*/
        }
    }

    @Nullable @Override public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving) {
        return setBlock(pos, state, isMoving);
    }

    @Override public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
        addCubeTileEntity(pos, tileEntityIn);
    }

    @Override public void addCubeTileEntity(BlockPos pos, TileEntity tileEntityIn) {
        tileEntityIn.setPos(pos);
        this.tileEntities.put(pos, tileEntityIn);
    }

    @Override public void removeTileEntity(BlockPos pos) {
        removeCubeTileEntity(pos);
    }

    @Override public void removeCubeTileEntity(BlockPos pos) {
        this.tileEntities.remove(pos);
        //TODO: reimplement deferredtileentities
        //this.deferredTileEntities.remove(pos);
    }

    @Deprecated
    @Override public void addEntity(Entity entityIn) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Deprecated
    @Override public Set<BlockPos> getTileEntitiesPos() {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Deprecated
    @Override public ChunkSection[] getSections() {
        throw new UnsupportedOperationException("How even?");
    }

    @Deprecated
    @Override public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Deprecated
    @Override public void setHeightmap(Heightmap.Type type, long[] data) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Deprecated
    @Override public Heightmap getHeightmap(Heightmap.Type typeIn) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Deprecated
    @Override public int getTopBlockY(Heightmap.Type heightmapType, int x, int z) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Deprecated
    @Override public ChunkPos getPos() {
        throw new UnsupportedOperationException("This should never be called!");
    }

    @Deprecated
    @Override public void setLastSaveTime(long saveTime) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Deprecated
    @Override public Map<String, StructureStart> getStructureStarts() {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Deprecated
    @Override public void setStructureStarts(Map<String, StructureStart> structureStartsIn) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Deprecated
    @Nullable @Override public BiomeContainer getBiomes() {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public void setModified(boolean modified) {
        setDirty(modified);
    }

    @Override public boolean isModified() {
        return isDirty();
    }


    @Override public void setDirty(boolean modified) {
        this.modified = modified;
    }

    @Override public boolean isDirty() {
        return modified;
    }

    @Override public ChunkStatus getStatus() {
        return getCubeStatus();
    }

    @Override public ChunkStatus getCubeStatus() {
        return this.status;
    }
    @Override
    public void setCubeStatus(ChunkStatus status)
    {
        this.status = status;
    }

    public CubePos getCubePos()
    {
        return this.cubePos;
    }

    public void setStatus(ChunkStatus status) {
        this.status = status;
    }

    @Override public ShortList[] getPackedPositions() {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Nullable @Override public CompoundNBT getDeferredTileEntity(BlockPos pos) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Nullable @Override public CompoundNBT getTileEntityNBT(BlockPos pos) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public Stream<BlockPos> getLightSources() {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public ITickList<Block> getBlocksToBeTicked() {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public ITickList<Fluid> getFluidsToBeTicked() {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public UpgradeData getUpgradeData() {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public void setInhabitedTime(long newInhabitedTime) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public long getInhabitedTime() {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public boolean hasLight() {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public void setLight(boolean lightCorrectIn) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Nullable @Override public StructureStart getStructureStart(String stucture) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public void putStructureStart(String structureIn, StructureStart structureStartIn) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public LongSet getStructureReferences(String structureIn) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public void addStructureReference(String strucutre, long reference) {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public Map<String, LongSet> getStructureReferences() {
        throw new UnsupportedOperationException("For later implementation");
    }

    @Override public void setStructureReferences(Map<String, LongSet> p_201606_1_) {
        throw new UnsupportedOperationException("For later implementation");
    }
}
