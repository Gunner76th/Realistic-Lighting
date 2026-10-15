package com.example.realisticlighting.block;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

/** Low-profile enclosed ceiling light; shares the industrial fixture's dye, bulb, GUI and electrical behavior. */
public class SquareCeilingFixtureBlock extends IndustrialFixtureBlock {
    public SquareCeilingFixtureBlock(Properties properties) { super(properties); }

    // The block model is 12x12x3 pixels and rotates 180 degrees for ceiling placement.
    // Occupy only the upper four pixels of the block, leaving walking clearance below.
    private static final VoxelShape CEILING_SHAPE = box(2, 12, 2, 14, 16, 14);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CEILING_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return CEILING_SHAPE;
    }

    @Nullable @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Ceiling only: DOWN points from the supporting ceiling toward the room.
        return context.getClickedFace() == Direction.DOWN
                ? defaultBlockState().setValue(FACING, Direction.DOWN)
                : null;
    }
}
