
package com.example.realisticlighting;

import com.example.realisticlighting.config.EnergyConfig;
import net.neoforged.fml.config.ModConfig;
import com.example.realisticlighting.registry.ModBlocks;
import com.example.realisticlighting.registry.ModItems;
import com.example.realisticlighting.registry.ModBlockEntities;
import com.example.realisticlighting.registry.ModMenus;
import com.example.realisticlighting.registry.ModCreativeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(RealisticLighting.MOD_ID)
public final class RealisticLighting {

    public static final String MOD_ID = "realistic_lighting";

    public RealisticLighting(
            IEventBus modEventBus,
            ModContainer modContainer
    ) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
		ModCreativeTabs.CREATIVE_TABS.register(modEventBus);		
		ModMenus.MENUS.register(modEventBus);
		
		modContainer.registerConfig(ModConfig.Type.COMMON, EnergyConfig.SPEC);
        
		modEventBus.addListener(this::registerCapabilities);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.LAMP_BASE.get(),
                (lamp, side) -> lamp.getEnergyStorage()
        );
    }
}
