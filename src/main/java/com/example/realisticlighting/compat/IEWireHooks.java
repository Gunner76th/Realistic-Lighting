package com.example.realisticlighting.compat;
import com.example.realisticlighting.blockentity.LampBaseBlockEntity;
import com.example.realisticlighting.blockentity.IELVFixtureBlockEntity;
public final class IEWireHooks {
    public static void disconnect(LampBaseBlockEntity lamp) {
        if (lamp instanceof IELVFixtureBlockEntity ie) ie.disconnectWires();
    }
}
