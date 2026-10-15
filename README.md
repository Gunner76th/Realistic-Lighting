# Realistic Lighting — v1.1.1

A Minecraft Java Edition 1.20.1 Forge mod featuring powered, replaceable-bulb lighting fixtures.

## Features

- Industrial Cage Fixture, Square Flush-Mount Ceiling Light, and Wall-Mounted Sconce, each dyeable in 16 colors.
- Incandescent (20 FE/t), fluorescent (10 FE/t), and LED (5 FE/t) bulbs with finite active-use lifespans (30, 90, and 300 Minecraft days respectively).
- 100 FE internal buffer per fixture; GUI for bulb management and On / Off / Redstone modes.
- Immersive Engineering LV wire connections and Ender IO energy/redstone conduit compatibility.
- English, Spanish (Spain), and Russian translations.
- Six crafting recipes that automatically adapt to installed technology mods.

## Requirements

- Minecraft Java Edition 1.20.1
- Minecraft Forge 47.x (47.4.10 recommended (minimum declared: 47.2.0))
- Java 17 to build/run in the supported environment

Immersive Engineering and Ender IO are **optional gameplay integrations**. JEI is optional. Tested compatibility versions: Immersive Engineering 10.2.0-183 and Ender IO 6.2.18-beta (Minecraft 1.20.1).

**Build note:** The source directly references the Immersive Engineering API, so **its JAR is required at compile time even when the mod is optional at runtime**. Obtain the IE JAR separately and place it at `libs/ImmersiveEngineering-1.20.1-10.2.0-183.jar`, or pass `-PieJar=/path/to/ImmersiveEngineering-1.20.1-10.2.0-183.jar` to Gradle. No third-party mod JAR is bundled.

## Build

On Windows, from the source directory:

```bat
gradlew.bat clean build
```

On Linux/macOS:

```sh
./gradlew clean build
```

The compiled mod JAR will be in `build/libs/`. The provided source archive is **not** itself a drop-in Minecraft mod JAR.

## Conditional crafting

The same six recipe outputs select a different ingredient set for each combination: both IE and Ender IO, IE only, Ender IO only, or neither (vanilla fallback). Recipe availability updates when the mod set changes and the world/datapacks are reloaded. JEI should show the matching recipes.

## Installation

Install Forge for Minecraft 1.20.1, then place the compiled Realistic Lighting mod JAR in the instance's `mods` folder. Add any optional integration mods separately.

## Compatibility and saves

The retired legacy Lamp Base was removed in v1.0.8; back up older worlds containing it before upgrading. Existing fixture inventories and settings were tested for persistence during development. Back up worlds before upgrading or changing mod sets.

## Verification

The developer's in-game testing confirmed recipe appearance across all four combinations of IE/Ender IO, the three fixture types, lighting controls, FE buffers, conduit wiring, and the localized GUI. This release source has undergone static resource checks; a fresh automated Forge build and full clean-install regression test have **not** been performed in this packaging environment.

## License

MIT — see `LICENSE`.

## Changes in 1.1.1 stable

- Added Grow Bulb (default 15 FE/t, 90 Minecraft days) with crop growth acceleration and diminishing returns.
- Added directional LED supplemental lighting with approximately 29-block tested horizontal reach in open terrain.
- Refined the LED light pattern to 16 staggered directions across six rings.
- Added restart-only COMMON TOML settings for four bulb FE rates and three fixture FE buffers, with documented defaults.
- Added fixture-specific GUI titles and adjusted bulb slot layout.

## Stable release status

Version 1.1.1 is the stable source promoted from RC.1 after user-reported successful clean build, client startup, dedicated-server startup, existing-world upgrade, multiplayer connection/placement, multiplayer GUI/LED switching, all four bulb types, and Grow Bulb crop acceleration. Simultaneous two-player synchronization and LED chunk-boundary edge cases have not been independently verified.

The Forge build targets 47.4.10; the metadata still permits 47.2.0+, which is **not yet fully verified**.
