package com.example.realisticlighting.logic;

import com.example.realisticlighting.block.LampBaseBlock;
import com.example.realisticlighting.blockentity.LampBaseBlockEntity;
import com.example.realisticlighting.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/** Server-side grow bulb bonus, with diminishing returns in overlapping coverage. */
public final class GrowLampGrowth {
    private GrowLampGrowth() {}

    public static void apply(Level level, BlockPos lamp) {
        if (!(level instanceof ServerLevel server)) return;
        List<BlockPos> eligible = new ArrayList<>();
        for (int depth = 1; depth <= 5; depth++) {
            int y = lamp.getY() - depth;
            for (int dx = -depth; dx <= depth; dx++) for (int dz = -depth; dz <= depth; dz++) {
                if (dx * dx + dz * dz > depth * depth) continue;
                BlockPos p = new BlockPos(lamp.getX() + dx, y, lamp.getZ() + dz);
                if (!server.hasChunkAt(p)) continue;
                BlockState state = server.getBlockState(p);
                if (state.isRandomlyTicking() && (state.getBlock() instanceof CropBlock
                        || state.getBlock() instanceof StemBlock || state.getBlock() instanceof NetherWartBlock))
                    eligible.add(p);
            }
        }
        if (eligible.isEmpty()) return;
        BlockPos target = eligible.get(server.random.nextInt(eligible.size()));
        int coverage = countActiveLamps(server, target);
        if (coverage == 0) return;
        // Each lamp attempts one bonus tick per second. For n overlapping lamps, each
        // attempt is accepted with probability 2*(1 - 0.5^n)/n. The combined expected
        // bonus is 1, 1.5, 1.75, 1.875... attempts (never more than 2).
        // This bounds stacking without globally modifying randomTickSpeed.
        double acceptance = 2.0 * (1.0 - Math.pow(0.5, coverage)) / coverage;
        if (server.random.nextDouble() >= acceptance) return;
        BlockState state = server.getBlockState(target);
        if (state.isRandomlyTicking()) state.randomTick(server, target, server.random);
    }

    private static int countActiveLamps(ServerLevel level, BlockPos crop) {
        int count = 0;
        for (int depth = 1; depth <= 5; depth++) {
            int y = crop.getY() + depth;
            for (int dx = -depth; dx <= depth; dx++) for (int dz = -depth; dz <= depth; dz++) {
                if (dx * dx + dz * dz > depth * depth) continue;
                BlockPos p = new BlockPos(crop.getX() + dx, y, crop.getZ() + dz);
                if (!level.hasChunkAt(p)) continue;
                BlockState state = level.getBlockState(p);
                if (!state.hasProperty(LampBaseBlock.LIT) || !state.getValue(LampBaseBlock.LIT)) continue;
                if (level.getBlockEntity(p) instanceof LampBaseBlockEntity fixture
                        && fixture.getBulb().is(ModItems.GROW_BULB.get())) count++;
            }
        }
        return count;
    }
}
