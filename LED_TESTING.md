# Dev.8 adaptive LED lighting prototype

This iteration replaces the fixed 6/12/18/24-block straight-line nodes with adaptive surface-aware illumination. A downward-facing fixture searches for a floor up to 28 blocks below it, then places light nodes on unobstructed floor surfaces in rings at approximately 4, 8, 12, 16, 20, and 24 blocks from the fixture. The resulting vanilla light falloff is intended to extend useful reach toward 28 blocks, depending on terrain and obstacles. Horizontal and upward installations use directional patterns.

Test in a backed-up world. Install a powered LED in a ceiling fixture 5 blocks above a large, flat, open area. Wait at least 2 seconds. Test at night and compare with the fixture off. Repeat with an obstacle, two overlapping fixtures, a removed bulb, a broken fixture, and a saved/reloaded world.

The supplemental light is implemented as invisible blocks and remains a prototype. Its placement depends on unobstructed line of sight and valid empty positions. Do not treat this as production-ready until world/chunk edge cases are tested.
