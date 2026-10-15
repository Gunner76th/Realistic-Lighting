# Realistic Lighting 1.1.1-rc.1 — release gate

## Confirmed by developer in Dev.9 testing
- [x] Smooth LED pattern, ~29 block radius
- [x] Five concurrent powered LED fixtures, no apparent steady-state performance issue
- [x] Immediate cleanup on fixture OFF and LED bulb removal
- [x] Save/reload restores appropriate lighting
- [x] Wall obstacle test prevents lighting behind a dirt wall

## Required before stable 1.1.1
- [ ] Clean `gradlew.bat clean build` using Forge 47.4.10 and separately provided IE compile-time JAR
- [ ] Clean Minecraft client launch with RC JAR
- [ ] Dedicated server launch (no client-only class errors)
- [ ] Multiplayer: two players, energy sync, menus, LED placement and cleanup
- [ ] Upgrade backup of an existing Dev.9 world to RC, verify block IDs/inventories
- [ ] Test all three fixture mount orientations and all four bulb types
- [ ] Test LED sources near chunk boundaries and when source/target chunks unload independently
- [ ] Test LED overlap cleanup when only one of two fixtures is disabled
- [ ] Check conditional recipes with/without IE and Ender IO; JEI if installed
- [ ] Validate English, Spanish, Russian UI text and config comments
- [ ] Confirm server restart required for COMMON config changes
- [ ] Update version to `1.1.1` only after passing gates; rebuild and verify JAR metadata

## Build output
`build/libs/realistic_lighting-1.1.1-rc.1.jar` (after successful Gradle build).

## Distribution
This source archive excludes Gradle caches, build output, game run directories, and third-party mod JARs. Supply your own Immersive Engineering JAR in `libs/` for compilation.
