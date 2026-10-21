
package com.example.realisticlighting.registry;

import com.example.realisticlighting.RealisticLighting;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Creative inventory listing; JEI also discovers these registered item stacks. */
public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(
                    Registries.CREATIVE_MODE_TAB,
                    RealisticLighting.MOD_ID
            );

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> REALISTIC_LIGHTING =
            CREATIVE_TABS.register(
                    "realistic_lighting",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.realistic_lighting"))
                            .icon(() -> new ItemStack(ModItems.INDUSTRIAL_FIXTURE_ITEM.get()))
                            .displayItems((parameters, output) -> {
                                output.accept(ModItems.INDUSTRIAL_FIXTURE_ITEM.get());
                                output.accept(ModItems.SQUARE_CEILING_FIXTURE_ITEM.get());
                                output.accept(ModItems.WALL_SCONCE_ITEM.get());
                                output.accept(ModItems.INCANDESCENT_BULB.get());
                                output.accept(ModItems.FLUORESCENT_BULB.get());
                                output.accept(ModItems.LED_BULB.get());
                                output.accept(ModItems.GROW_BULB.get());
                            })
                            .build()
            );

    private ModCreativeTabs() {}
}
