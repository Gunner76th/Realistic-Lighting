
package com.example.realisticlighting.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

public class SquareCeilingFixtureBlock extends IndustrialFixtureBlock {

    private static final VoxelShape SHAPE =
            box(2, 12, 2, 14, 16, 14);

    public SquareCeilingFixtureBlock(Properties properties) {
        super(properties);

        registerDefaultState(
                defaultBlockState()
                        .setValue(FACING, Direction.DOWN)
        );
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            net.minecraft.core.BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(
            BlockPlaceContext context
    ) {
        return defaultBlockState()
                .setValue(FACING, Direction.DOWN);
    }
}
