package com.example.realisticlighting.registry;

import com.example.realisticlighting.RealisticLighting;
import com.example.realisticlighting.block.LampBaseBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RealisticLighting.MOD_ID);
    public static final RegistryObject<Block> INDUSTRIAL_FIXTURE = BLOCKS.register("industrial_fixture", () -> new com.example.realisticlighting.block.IndustrialFixtureBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(1.5F).sound(SoundType.METAL).noOcclusion().lightLevel(state -> state.getValue(LampBaseBlock.LIT) ? 15 : 0)));
    public static final RegistryObject<Block> SQUARE_CEILING_FIXTURE = BLOCKS.register("square_ceiling_fixture", () -> new com.example.realisticlighting.block.SquareCeilingFixtureBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(1.5F).sound(SoundType.METAL).noOcclusion().lightLevel(state -> state.getValue(LampBaseBlock.LIT) ? 15 : 0)));
    public static final RegistryObject<Block> WALL_SCONCE = BLOCKS.register("wall_sconce", () -> new com.example.realisticlighting.block.WallSconceBlock(Block.Properties.of().mapColor(MapColor.METAL).strength(1.5F).sound(SoundType.METAL).noOcclusion().lightLevel(state -> state.getValue(LampBaseBlock.LIT) ? 15 : 0)));
    public static final RegistryObject<Block> LED_LIGHT_NODE = BLOCKS.register("led_light_node", () ->
        new com.example.realisticlighting.block.LedLightNodeBlock(Block.Properties.of().noCollission().noOcclusion().noLootTable()
            .strength(0.0F).lightLevel(state -> state.getValue(com.example.realisticlighting.block.LedLightNodeBlock.LIGHT))));
    private ModBlocks() {}
}
