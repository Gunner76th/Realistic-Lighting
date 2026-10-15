package com.example.realisticlighting.registry;

import com.example.realisticlighting.RealisticLighting;
import com.example.realisticlighting.blockentity.LampBaseBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.ModList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RealisticLighting.MOD_ID);
    public static final RegistryObject<BlockEntityType<LampBaseBlockEntity>> LAMP_BASE = BLOCK_ENTITIES.register("lamp_base", () -> BlockEntityType.Builder.of(ModBlockEntities::createFixture, ModBlocks.INDUSTRIAL_FIXTURE.get(), ModBlocks.SQUARE_CEILING_FIXTURE.get(), ModBlocks.WALL_SCONCE.get()).build(null));
    public static final RegistryObject<BlockEntityType<com.example.realisticlighting.blockentity.LedLightNodeBlockEntity>> LED_LIGHT_NODE = BLOCK_ENTITIES.register("led_light_node", () ->
        BlockEntityType.Builder.of(com.example.realisticlighting.blockentity.LedLightNodeBlockEntity::new, ModBlocks.LED_LIGHT_NODE.get()).build(null));
    public static LampBaseBlockEntity createFixture(BlockPos pos, BlockState state) {
        if (ModList.get().isLoaded("immersiveengineering")) return IEFactory.create(pos, state);
        return new LampBaseBlockEntity(pos, state);
    }
    /** Separate class prevents loading IE types when IE is absent. */
    private static class IEFactory {
        static LampBaseBlockEntity create(BlockPos pos, BlockState state) {
            return new com.example.realisticlighting.blockentity.IELVFixtureBlockEntity(pos, state);
        }
    }
    private ModBlockEntities() {}
}
