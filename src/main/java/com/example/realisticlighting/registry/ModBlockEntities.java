
package com.example.realisticlighting.registry;

import com.example.realisticlighting.RealisticLighting;
import com.example.realisticlighting.blockentity.LampBaseBlockEntity;
import com.example.realisticlighting.blockentity.LedLightNodeBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(
                    Registries.BLOCK_ENTITY_TYPE,
                    RealisticLighting.MOD_ID
            );

    public static final DeferredHolder<
            BlockEntityType<?>,
            BlockEntityType<LampBaseBlockEntity>
    > LAMP_BASE = BLOCK_ENTITIES.register(
            "lamp_base",
            () -> BlockEntityType.Builder.of(
                    ModBlockEntities::createFixture,
                    ModBlocks.INDUSTRIAL_FIXTURE.get(),
                    ModBlocks.SQUARE_CEILING_FIXTURE.get(),
                    ModBlocks.WALL_SCONCE.get()
            ).build(null)
    );

    public static final DeferredHolder<
            BlockEntityType<?>,
            BlockEntityType<LedLightNodeBlockEntity>
    > LED_LIGHT_NODE = BLOCK_ENTITIES.register(
            "led_light_node",
            () -> BlockEntityType.Builder.of(
                    LedLightNodeBlockEntity::new,
                    ModBlocks.LED_LIGHT_NODE.get()
            ).build(null)
    );

    public static LampBaseBlockEntity createFixture(
            BlockPos pos,
            BlockState state
    ) {
        // Core migration stage:
        // IE-specific fixture construction will be restored
        // when the NeoForge IE integration is ported.
        return new LampBaseBlockEntity(pos, state);
    }

    private ModBlockEntities() {
    }
}
