package com.example.realisticlighting.registry;

import com.example.realisticlighting.RealisticLighting;
import com.example.realisticlighting.block.IndustrialFixtureBlock;
import com.example.realisticlighting.block.LampBaseBlock;
import com.example.realisticlighting.block.LedLightNodeBlock;
import com.example.realisticlighting.block.SquareCeilingFixtureBlock;
import com.example.realisticlighting.block.WallSconceBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(RealisticLighting.MOD_ID);

private static Block.Properties properties(String name) {
    return Block.Properties.of();
}

    private static Block.Properties fixtureProperties(String name) {
        return properties(name)
                .mapColor(MapColor.METAL)
                .strength(1.5F)
                .sound(SoundType.METAL)
                .noOcclusion()
                .lightLevel(state ->
                        state.getValue(LampBaseBlock.LIT) ? 15 : 0);
    }

    public static final DeferredBlock<IndustrialFixtureBlock> INDUSTRIAL_FIXTURE =
            BLOCKS.register("industrial_fixture",
                    () -> new IndustrialFixtureBlock(
                            fixtureProperties("industrial_fixture")));

    public static final DeferredBlock<SquareCeilingFixtureBlock> SQUARE_CEILING_FIXTURE =
            BLOCKS.register("square_ceiling_fixture",
                    () -> new SquareCeilingFixtureBlock(
                            fixtureProperties("square_ceiling_fixture")));

    public static final DeferredBlock<WallSconceBlock> WALL_SCONCE =
            BLOCKS.register("wall_sconce",
                    () -> new WallSconceBlock(
                            fixtureProperties("wall_sconce")));

    public static final DeferredBlock<LedLightNodeBlock> LED_LIGHT_NODE =
            BLOCKS.register("led_light_node",
                    () -> new LedLightNodeBlock(
                            properties("led_light_node")
                                    .noCollission()
                                    .noOcclusion()
                                    .noLootTable()
                                    .strength(0.0F)
                                    .lightLevel(state ->
                                            state.getValue(
                                                    LedLightNodeBlock.LIGHT))));

    private ModBlocks() {}
}
