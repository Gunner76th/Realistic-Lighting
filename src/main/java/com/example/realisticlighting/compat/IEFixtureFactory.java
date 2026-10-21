
package com.example.realisticlighting.compat;

import com.example.realisticlighting.blockentity.IELVFixtureBlockEntity;
import com.example.realisticlighting.blockentity.LampBaseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Loaded only when Immersive Engineering is installed.
 */
public final class IEFixtureFactory {

    private IEFixtureFactory() {
    }

    public static LampBaseBlockEntity create(
            BlockPos pos,
            BlockState state
    ) {
        return new IELVFixtureBlockEntity(pos, state);
    }
}
