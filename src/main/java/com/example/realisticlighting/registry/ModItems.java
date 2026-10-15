package com.example.realisticlighting.registry;

import com.example.realisticlighting.RealisticLighting;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RealisticLighting.MOD_ID);
    public static final RegistryObject<Item> INCANDESCENT_BULB = ITEMS.register("incandescent_bulb", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> FLUORESCENT_BULB = ITEMS.register("fluorescent_bulb", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GROW_BULB = ITEMS.register("grow_bulb", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LED_BULB = ITEMS.register("led_bulb", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INDUSTRIAL_FIXTURE_ITEM = ITEMS.register("industrial_fixture", () -> new BlockItem(ModBlocks.INDUSTRIAL_FIXTURE.get(), new Item.Properties()));
    public static final RegistryObject<Item> SQUARE_CEILING_FIXTURE_ITEM = ITEMS.register("square_ceiling_fixture", () -> new BlockItem(ModBlocks.SQUARE_CEILING_FIXTURE.get(), new Item.Properties()));
    public static final RegistryObject<Item> WALL_SCONCE_ITEM = ITEMS.register("wall_sconce", () -> new BlockItem(ModBlocks.WALL_SCONCE.get(), new Item.Properties()));
    private ModItems() {}
}
