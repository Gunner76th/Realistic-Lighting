package com.example.realisticlighting.blockentity;

import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.wires.*;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Direction;
import com.example.realisticlighting.block.IndustrialFixtureBlock;
import com.example.realisticlighting.block.SquareCeilingFixtureBlock;
import com.example.realisticlighting.block.WallSconceBlock;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;

/** Loaded only when Immersive Engineering is installed. Both fixture blocks use this same BE. */
public class IELVFixtureBlockEntity extends LampBaseBlockEntity implements EnergyTransferHandler.EnergyConnector {
    private int wireCount;
    private final ConnectionPoint point;

    public IELVFixtureBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        point = new ConnectionPoint(pos, 0);
    }

    private int maxWireConnections() {
        return getBlockState().getBlock() instanceof SquareCeilingFixtureBlock ? 4 : 2;
    }

    @Override public boolean canConnect() { return true; }
    @Override public BlockPos getConnectionMaster(WireType type, TargetingInfo target) { return worldPosition; }
    @Override public ConnectionPoint getTargetedPoint(TargetingInfo target, Vec3i offset) { return point; }
    @Override public Collection<ConnectionPoint> getConnectionPoints() { return List.of(point); }
    @Override public BlockPos getPosition() { return worldPosition; }
    @Override public boolean canConnectCable(WireType wire, ConnectionPoint target, Vec3i offset) {
        return point.equals(target) && Vec3i.ZERO.equals(offset) && wireCount < maxWireConnections() &&
                (wire == WireType.COPPER || wire == WireType.COPPER_INSULATED);
    }
    @Override public void connectCable(WireType type, ConnectionPoint target, IImmersiveConnectable other, ConnectionPoint otherPoint) {
        wireCount = Math.min(maxWireConnections(), wireCount + 1);
        setChanged();
    }
    @Override public void removeCable(Connection connection, ConnectionPoint target) {
        wireCount = connection == null ? 0 : Math.max(0, wireCount - 1);
        setChanged();
    }
    @Override public Vec3 getConnectionOffset(ConnectionPoint here, ConnectionPoint there, WireType wire) {
        BlockState state = getBlockState();
        // IE calls this for each individual connection, passing the OTHER endpoint.
        // Select the edge facing that endpoint independently for each wire.
        // Values are block-local; y is just below the ceiling mounting surface.
        if (state.getBlock() instanceof SquareCeilingFixtureBlock) {
            BlockPos remote = there.position();
            int dx = remote.getX() - worldPosition.getX();
            int dz = remote.getZ() - worldPosition.getZ();
            if (Math.abs(dx) > Math.abs(dz))
                return new Vec3(dx > 0 ? .88 : .12, .88, .5);
            if (dz != 0)
                return new Vec3(.5, .88, dz > 0 ? .88 : .12);
            // Vertically aligned endpoints have no preferred horizontal edge.
            // Keep the old east-side terminal for that ambiguous case.
            return new Vec3(.88, .88, .5);
        }

        if (state.getBlock() instanceof WallSconceBlock) {
            // Each wire attaches to the ROUND WALL PLATE, never the arm/socket.
            // Project the remote endpoint onto the wall plane; choose the dominant
            // horizontal or vertical direction and place the contact on that rim.
            Direction outward = state.getValue(IndustrialFixtureBlock.FACING);
            BlockPos remote = there.position();
            int dx = remote.getX() - worldPosition.getX();
            int dy = remote.getY() - worldPosition.getY();
            int dz = remote.getZ() - worldPosition.getZ();
            double x = .5 - .405 * outward.getStepX();
            double y = .5;
            double z = .5 - .405 * outward.getStepZ();
            int lateral = outward.getAxis() == Direction.Axis.X ? dz : dx;
            if (Math.abs(dy) > Math.abs(lateral)) y += dy > 0 ? .32 : -.32;
            else if (lateral != 0) {
                if (outward.getAxis() == Direction.Axis.X) z += lateral > 0 ? .32 : -.32;
                else x += lateral > 0 ? .32 : -.32;
            } else y += .32;
            return new Vec3(x, y, z);
        }

        // FACING points away from the supporting surface. Put the cable terminal
        // on the opposite side, beside the mounting plate, for all six mounts.
        if (state.getBlock() instanceof IndustrialFixtureBlock) {
            Direction outward = state.getValue(IndustrialFixtureBlock.FACING);
            return new Vec3(.5 - .38 * outward.getStepX(),
                            .5 - .38 * outward.getStepY(),
                            .5 - .38 * outward.getStepZ());
        }
        // Legacy lamp base keeps its original attachment position.
        return new Vec3(.5, .82, .5);
    }
    @Override public Collection<ResourceLocation> getRequestedHandlers() { return List.of(EnergyTransferHandler.ID); }
    @Override public boolean isSource(ConnectionPoint at) { return false; }
    @Override public boolean isSink(ConnectionPoint at) { return point.equals(at); }
    @Override public int getRequestedEnergy() { return Math.max(0, getEnergyCapacity() - getEnergyStored()); }
    @Override public void insertEnergy(int amount) { receiveWireEnergy(amount); }

  
@Override
protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putInt("LVWireCount", wireCount);
}

@Override
protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    wireCount = Math.max(
            0,
            Math.min(maxWireConnections(), tag.getInt("LVWireCount"))
    );
}

    @Override public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) GlobalWireNetwork.getNetwork(level).onConnectorLoad(this, level);
    }
    @Override public void onChunkUnloaded() {
        if (level != null && !level.isClientSide) GlobalWireNetwork.getNetwork(level).onConnectorUnload(this);
        super.onChunkUnloaded();
    }
    @Override public void setRemoved() {
        if (level != null && !level.isClientSide) GlobalWireNetwork.getNetwork(level).onConnectorUnload(this);
        super.setRemoved();
    }
    
public void disconnectWires() {
    if (level == null || level.isClientSide) {
        return;
    }

    GlobalWireNetwork network = GlobalWireNetwork.getNetwork(level);

    network.removeAllConnectionsAt(this, connection -> {
        if (!level.getGameRules().getBoolean(
                net.minecraft.world.level.GameRules.RULE_DOBLOCKDROPS)) {
            return;
        }

        net.minecraft.world.item.ItemStack wire =
                connection.type.getWireCoil(connection);

        if (!wire.isEmpty()) {
            net.minecraft.world.level.block.Block.popResource(
                    level,
                    worldPosition,
                    wire
            );
        }
    });

    wireCount = 0;
    setChanged();
}
}