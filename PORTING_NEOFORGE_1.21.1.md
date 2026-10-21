# Realistic Lighting — NeoForge 1.21.1 port

Target: Minecraft 1.21.1, NeoForge, Java 21. Preserve Forge 1.20.1 v1.1.1 on `main`.

## Milestones

- [x] Create `neoforge-1.21.1` development branch from stable `main`.
- [ ] Establish NeoForge 1.21.1 ModDevGradle build and migrate metadata.
- [ ] Port mod initialization, registries, fixture blocks and bulb items.
- [ ] Port block entities, serialization, energy storage and capabilities.
- [ ] Port fixture menus, screens and client/server networking.
- [ ] Port extended LED lighting, ownership, cleanup and chunk boundaries.
- [ ] Port Grow Bulb behavior, configuration, recipes and translations.
- [ ] Validate Immersive Engineering, Ender IO and JEI integrations against their NeoForge 1.21.1 builds.
- [ ] Test singleplayer, dedicated server, multiplayer, existing saves and lighting edge cases.
- [ ] Release a separate 1.2.0 alpha after tests pass.

## Source audit notes

Current `build.gradle` uses ForgeGradle 6, Java 17, Forge 1.20.1-47.4.10, and an optional local IE 1.20.1 compile-time JAR. `RealisticLighting.java` registers through Forge's event bus. `LampBaseBlockEntity.java` uses ForgeCapabilities, LazyOptional, Forge EnergyStorage and block-entity sync. The existing extended LED algorithm is isolated in `logic/LedExtendedLighting.java`. No 1.20.1 dependency or compile-time IE JAR should be carried forward without checking the NeoForge 1.21.1 API.

## Development rule

Do not overwrite stable `main` or publish a 1.21.1 build until it compiles and passes client and dedicated-server tests.
