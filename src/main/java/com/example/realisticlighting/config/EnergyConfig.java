package com.example.realisticlighting.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.example.realisticlighting.RealisticLighting;

/** Values are snapshotted at server startup, never hot-reloaded during gameplay. */
@Mod.EventBusSubscriber(modid = RealisticLighting.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class EnergyConfig {
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.IntValue INCANDESCENT, FLUORESCENT, LED, GROW;
    private static final ForgeConfigSpec.IntValue CAGE, CEILING, SCONCE;
    private static volatile int incandescent=20, fluorescent=10, led=5, grow=15;
    private static volatile int cage=100, ceiling=100, sconce=100;
    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("energy");
        INCANDESCENT = b.comment("Incandescent bulb energy consumption in FE/t. Default: 20 FE/t. Restart the client/server to apply changes.").defineInRange("incandescentFEPerTick",20,1,100000);
        FLUORESCENT = b.comment("Fluorescent bulb energy consumption in FE/t. Default: 10 FE/t. Restart the client/server to apply changes.").defineInRange("fluorescentFEPerTick",10,1,100000);
        LED = b.comment("LED bulb energy consumption in FE/t. Default: 5 FE/t. Restart the client/server to apply changes.").defineInRange("ledFEPerTick",5,1,100000);
        GROW = b.comment("Grow Lamp energy consumption in FE/t. Default: 15 FE/t. Restart the client/server to apply changes.").defineInRange("growLightFEPerTick",15,1,100000);
        CAGE = b.comment("Industrial Cage Fixture energy buffer capacity. Default: 100 FE. Restart the client/server to apply changes.").defineInRange("industrialCageBuffer",100,1,1000000);
        CEILING = b.comment("Square Flush-Mount Ceiling Light energy buffer capacity. Default: 100 FE. Restart the client/server to apply changes.").defineInRange("squareCeilingBuffer",100,1,1000000);
        SCONCE = b.comment("Wall-Mounted Sconce energy buffer capacity. Default: 100 FE. Restart the client/server to apply changes.").defineInRange("wallSconceBuffer",100,1,1000000);
        b.pop();
        SPEC = b.build();
    }
    private EnergyConfig() {}
    @SubscribeEvent
    public static void serverStarting(ServerAboutToStartEvent event) {
        incandescent=INCANDESCENT.get(); fluorescent=FLUORESCENT.get();
        led=LED.get(); grow=GROW.get();
        cage=CAGE.get(); ceiling=CEILING.get(); sconce=SCONCE.get();
    }
    public static int incandescent() { return incandescent; }
    public static int fluorescent() { return fluorescent; }
    public static int led() { return led; }
    public static int grow() { return grow; }
    public static int cage() { return cage; }
    public static int ceiling() { return ceiling; }
    public static int sconce() { return sconce; }
}
