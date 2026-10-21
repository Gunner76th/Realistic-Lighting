package com.example.realisticlighting.registry;

import com.example.realisticlighting.RealisticLighting;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(RealisticLighting.MOD_ID);

private static Item.Properties properties(String name) {
    return new Item.Properties();
}

    public static final DeferredItem<Item> INCANDESCENT_BULB =
            ITEMS.register("incandescent_bulb",
                    () -> new Item(properties("incandescent_bulb")));

    public static final DeferredItem<Item> FLUORESCENT_BULB =
            ITEMS.register("fluorescent_bulb",
                    () -> new Item(properties("fluorescent_bulb")));

    public static final DeferredItem<Item> GROW_BULB =
            ITEMS.register("grow_bulb",
                    () -> new Item(properties("grow_bulb")));

    public static final DeferredItem<Item> LED_BULB =
            ITEMS.register("led_bulb",
                    () -> new Item(properties("led_bulb")));

    public static final DeferredItem<BlockItem> INDUSTRIAL_FIXTURE_ITEM =
            ITEMS.register("industrial_fixture",
                    () -> new BlockItem(
                            ModBlocks.INDUSTRIAL_FIXTURE.get(),
                            properties("industrial_fixture")));

    public static final DeferredItem<BlockItem> SQUARE_CEILING_FIXTURE_ITEM =
            ITEMS.register("square_ceiling_fixture",
                    () -> new BlockItem(
                            ModBlocks.SQUARE_CEILING_FIXTURE.get(),
                            properties("square_ceiling_fixture")));

    public static final DeferredItem<BlockItem> WALL_SCONCE_ITEM =
            ITEMS.register("wall_sconce",
                    () -> new BlockItem(
                            ModBlocks.WALL_SCONCE.get(),
                            properties("wall_sconce")));

    private ModItems() {}
}
