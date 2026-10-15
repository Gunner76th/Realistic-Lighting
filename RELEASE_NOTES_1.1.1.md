# Realistic Lighting 1.1.1 — Stable Release Notes

Minecraft Java 1.20.1 · Forge 47.4.10 build target · Java 17

## Highlights
- Grow Bulb: 15 FE/t default, 90-day lifespan, directional crop-growth acceleration with diminishing returns.
- LED Bulb: adaptive, directional supplemental illumination, with an approximately 29-block measured radius; smoother 16-direction/six-ring distribution.
- Industrial Cage, Square Flush-Mount Ceiling Light, and Wall-Mounted Sconce retain existing registrations, wiring and controls.
- Fixture-specific GUI titles and improved bulb-slot placement.
- Restart-only COMMON TOML settings for four bulb FE/t rates and three fixture energy buffers, with explicit default-value comments.
- English, Spanish, and Russian localization and optional integration features retained.

## Testing reported by developer
- Gradle build of 1.1.1-rc.1 succeeded.
- Client and dedicated-server startup succeeded.
- Existing world upgraded successfully.
- Multiplayer connection, placement, GUI and LED on/off worked.
- All four bulb types recognized; Grow Bulb accelerated crops.
- Dev.9 LED tests: ~29-block radius, five fixtures operating, instant cleanup on off/bulb removal, save/reload, and obstruction handling.

## Known validation limits
- Final 1.1.1 rebuild must be verified independently from the source ZIP.
- Simultaneous two-player light synchronization and chunk-boundary unload/reload edge cases have not been independently tested.
- The adaptive LED algorithm uses supplemental invisible light-source blocks. Back up worlds before upgrading.

## Installation
Install the built `realistic_lighting-1.1.1.jar` in the mods directory of a Forge 1.20.1 instance. Remove older Realistic Lighting JARs to avoid duplicates. Configuration changes take effect after client/server restart.
