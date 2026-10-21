
package com.example.realisticlighting.config;

import com.example.realisticlighting.RealisticLighting;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/**
 * Energy values are snapshotted at server startup.
 * Configuration changes are not applied during gameplay.
 */
@EventBusSubscriber(
        modid = RealisticLighting.MOD_ID
)
public final class EnergyConfig {

    public static final ModConfigSpec SPEC;

    private static final ModConfigSpec.IntValue
            INCANDESCENT, FLUORESCENT, LED, GROW;

    private static final ModConfigSpec.IntValue
            CAGE, CEILING, SCONCE;

    private static volatile int incandescent = 20;
    private static volatile int fluorescent = 10;
    private static volatile int led = 5;
    private static volatile int grow = 15;

    private static volatile int cage = 100;
    private static volatile int ceiling = 100;
    private static volatile int sconce = 100;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();

        b.push("energy");

        INCANDESCENT = b.comment(
                "Incandescent bulb energy consumption in FE/t. Default: 20 FE/t. Restart to apply changes."
        ).defineInRange("incandescentFEPerTick", 20, 1, 100000);

        FLUORESCENT = b.comment(
                "Fluorescent bulb energy consumption in FE/t. Default: 10 FE/t. Restart to apply changes."
        ).defineInRange("fluorescentFEPerTick", 10, 1, 100000);

        LED = b.comment(
                "LED bulb energy consumption in FE/t. Default: 5 FE/t. Restart to apply changes."
        ).defineInRange("ledFEPerTick", 5, 1, 100000);

        GROW = b.comment(
                "Grow Lamp energy consumption in FE/t. Default: 15 FE/t. Restart to apply changes."
        ).defineInRange("growLightFEPerTick", 15, 1, 100000);

        CAGE = b.comment(
                "Industrial Cage Fixture energy buffer capacity. Default: 100 FE. Restart to apply changes."
        ).defineInRange("industrialCageBuffer", 100, 1, 1000000);

        CEILING = b.comment(
                "Square Flush-Mount Ceiling Light energy buffer capacity. Default: 100 FE. Restart to apply changes."
        ).defineInRange("squareCeilingBuffer", 100, 1, 1000000);

        SCONCE = b.comment(
                "Wall-Mounted Sconce energy buffer capacity. Default: 100 FE. Restart to apply changes."
        ).defineInRange("wallSconceBuffer", 100, 1, 1000000);

        b.pop();

        SPEC = b.build();
    }

    private EnergyConfig() {}

    @SubscribeEvent
    public static void serverStarting(ServerAboutToStartEvent event) {
        incandescent = INCANDESCENT.get();
        fluorescent = FLUORESCENT.get();
        led = LED.get();
        grow = GROW.get();

        cage = CAGE.get();
        ceiling = CEILING.get();
        sconce = SCONCE.get();
    }

    public static int incandescent() {
        return incandescent;
    }

    public static int fluorescent() {
        return fluorescent;
    }

    public static int led() {
        return led;
    }

    public static int grow() {
        return grow;
    }

    public static int cage() {
        return cage;
    }

    public static int ceiling() {
        return ceiling;
    }

    public static int sconce() {
        return sconce;
    }
}
