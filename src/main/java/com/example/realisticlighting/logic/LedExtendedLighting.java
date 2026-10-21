package com.example.realisticlighting.logic;

import com.example.realisticlighting.block.IndustrialFixtureBlock;
import com.example.realisticlighting.block.WallSconceBlock;
import com.example.realisticlighting.blockentity.LedLightNodeBlockEntity;
import com.example.realisticlighting.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.HashSet;
import java.util.Set;

/** Adaptive, surface-aware directional LED illumination. Server-side only. */
public final class LedExtendedLighting {
    private static final int[] RADII = {4, 8, 12, 16, 20, 24};
    private static final int[] LEVELS = {15, 14, 12, 10, 8, 6};
    private static final int ANGULAR_SAMPLES = 16;
    // Interleave neighboring rings to avoid the eight bright radial spokes of Dev.8.
    // The outer radius and light levels stay unchanged to preserve the tested reach.
    private LedExtendedLighting() {}

    public static Set<BlockPos> desired(Level level, BlockPos fixture, BlockState fixtureState) {
        Set<BlockPos> result = new HashSet<>();
        if (!fixtureState.hasProperty(IndustrialFixtureBlock.FACING)) return result;
        Direction facing = fixtureState.getValue(IndustrialFixtureBlock.FACING);
        boolean sconce = fixtureState.getBlock() instanceof WallSconceBlock;
        // Place downward light close to the illuminated surface, not at a fixed depth
        // below the fixture. This works for ordinary 3-5 block room heights.
        int floorY = findFloor(level, fixture.below(), 28);
        for (int i = 0; i < RADII.length; i++) {
            int radius = RADII[i];
            for (int sample = 0; sample < ANGULAR_SAMPLES; sample++) {
                double angle = (sample + (i % 2 == 0 ? 0.0 : 0.5)) * (2.0 * Math.PI / ANGULAR_SAMPLES);
                int dx = (int)Math.round(Math.cos(angle) * radius);
                int dz = (int)Math.round(Math.sin(angle) * radius);
                BlockPos target;
                if (facing == Direction.DOWN) {
                    if (floorY == Integer.MIN_VALUE) continue;
                    target = findSurfaceAir(level, fixture.offset(dx, 0, dz), floorY);
                } else if (facing == Direction.UP) {
                    // Upward installations spread their beam horizontally while rising.
                    target = fixture.offset(dx, Math.max(1, radius / 2), dz);
                } else {
                    // Forward cone: spread sideways and downward from the wall fixture.
                    int forward = radius;
                    int sideways = dx / 2;
                    int drop = Math.max(1, radius / 4);
                    target = fixture.relative(facing, forward).relative(facing.getClockWise(), sideways).below(drop);
                    // Keep the wall fixture beam in the forward hemisphere.
                    if (Math.sin(angle) < 0) continue;
                }
                if (target == null || !level.isInWorldBounds(target) || !level.hasChunkAt(target)) continue;
                BlockState at = level.getBlockState(target);
                if (!at.isAir() && !at.is(ModBlocks.LED_LIGHT_NODE.get())) continue;
                if (!clearRay(level, fixture, target, facing)) continue;
                result.add(target.immutable());
            }
        }
        return result;
    }

    private static int findFloor(Level level, BlockPos start, int maxDrop) {
        for (int i = 0; i < maxDrop; i++) {
            BlockPos p = start.below(i);
            if (!level.isInWorldBounds(p) || !level.hasChunkAt(p)) return Integer.MIN_VALUE;
            if (level.getBlockState(p).isCollisionShapeFullBlock(level, p)) return p.getY();
        }
        return Integer.MIN_VALUE;
    }

    private static BlockPos findSurfaceAir(Level level, BlockPos column, int referenceFloor) {
        // Follow modest terrain variation; never drill through a roof or wall.
        for (int y = referenceFloor + 3; y >= referenceFloor - 3; y--) {
            BlockPos ground = new BlockPos(column.getX(), y, column.getZ());
            BlockPos above = ground.above();
            if (!level.isInWorldBounds(above) || !level.hasChunkAt(ground)) return null;
            if (level.getBlockState(ground).isCollisionShapeFullBlock(level, ground)
                && (level.getBlockState(above).isAir() || level.getBlockState(above).is(ModBlocks.LED_LIGHT_NODE.get())))
                return above;
        }
        return null;
    }



private static boolean clearRay(
        Level level,
        BlockPos fixture,
        BlockPos target,
        Direction facing
) {
    Vec3 from = Vec3.atCenterOf(fixture).add(
            facing.getStepX() * 0.65,
            facing.getStepY() * 0.65,
            facing.getStepZ() * 0.65
    );

    Vec3 to = Vec3.atCenterOf(target);

    // Use the block-only ray tracer. Unlike ClipContext with a
    // null entity, this does not construct an EntityCollisionContext.
    BlockHitResult hit = level.clip(
            new ClipContext(
                    from,
                    to,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    net.minecraft.world.phys.shapes.CollisionContext.empty()
            )
    );

    return hit.getType() == HitResult.Type.MISS
            || hit.getBlockPos().equals(target);
}



    public static int strength(BlockPos fixture, BlockPos node, BlockState state) {
        Direction facing = state.getValue(IndustrialFixtureBlock.FACING);
        double distance = facing.getAxis() == Direction.Axis.Y
            ? Math.hypot(node.getX() - fixture.getX(), node.getZ() - fixture.getZ())
            : Math.abs((node.getX() - fixture.getX()) * facing.getStepX() + (node.getZ() - fixture.getZ()) * facing.getStepZ());
        for (int i = 0; i < RADII.length; i++) if (distance <= RADII[i] + 1) return LEVELS[i];
        return LEVELS[LEVELS.length - 1];
    }

    public static void remove(Level level, BlockPos fixture, Set<BlockPos> positions) {
        for (BlockPos p : positions) {
            if (!level.hasChunkAt(p)) continue;
            if (level.getBlockEntity(p) instanceof LedLightNodeBlockEntity node) node.removeOwner(fixture);
        }
    }

    public static void add(Level level, BlockPos fixture, BlockState fixtureState, Set<BlockPos> positions) {
        for (BlockPos p : positions) {
            if (!level.hasChunkAt(p)) continue;
            BlockState at = level.getBlockState(p);
            if (at.isAir()) level.setBlock(p, ModBlocks.LED_LIGHT_NODE.get().defaultBlockState(), 3);
            if (level.getBlockEntity(p) instanceof LedLightNodeBlockEntity node)
                node.addOwner(fixture, strength(fixture, p, fixtureState));
        }
    }
}
