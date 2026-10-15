package com.example.realisticlighting;

import com.example.realisticlighting.registry.ModBlockEntities;
import com.example.realisticlighting.registry.ModBlocks;
import com.example.realisticlighting.registry.ModItems;
import com.example.realisticlighting.registry.ModMenus;
import com.example.realisticlighting.registry.ModCreativeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import com.example.realisticlighting.config.EnergyConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RealisticLighting.MOD_ID)
public class RealisticLighting {
    public static final String MOD_ID = "realistic_lighting";
    public RealisticLighting() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EnergyConfig.SPEC);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        ModCreativeTabs.CREATIVE_TABS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModMenus.MENUS.register(bus);
    }
}
