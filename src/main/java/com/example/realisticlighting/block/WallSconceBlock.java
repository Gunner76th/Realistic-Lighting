package com.example.realisticlighting.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Downward exposed-bulb wall sconce, mounted to any horizontal wall. */
public class WallSconceBlock extends IndustrialFixtureBlock {
    public WallSconceBlock(Properties properties) { super(properties); }

    // Selection and collision are anchored to the round mounting plate, not the
    // outward-projecting arm. The base model faces SOUTH and has its plate at Z=0;
    // the blockstate's Y rotations move that plate to the other three wall faces.
    // A 10x10-pixel target covers the plate, with 3 pixels of wall-normal depth.
    private static final VoxelShape SOUTH = box(3, 3, 0, 13, 13, 3);
    private static final VoxelShape NORTH = box(3, 3, 13, 13, 13, 16);
    private static final VoxelShape WEST  = box(13, 3, 3, 16, 13, 13);
    private static final VoxelShape EAST  = box(0, 3, 3, 3, 13, 13);

    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> SOUTH;
        };
    }
    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }
    @Nullable @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        return face.getAxis().isHorizontal() ? defaultBlockState().setValue(FACING, face) : null;
    }
}
