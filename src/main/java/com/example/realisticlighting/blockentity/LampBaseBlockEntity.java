package com.example.realisticlighting.blockentity;

import com.example.realisticlighting.block.LampBaseBlock;
import com.example.realisticlighting.block.SquareCeilingFixtureBlock;
import com.example.realisticlighting.block.WallSconceBlock;
import com.example.realisticlighting.config.EnergyConfig;
import com.example.realisticlighting.block.IndustrialFixtureBlock;
import com.example.realisticlighting.registry.ModBlockEntities;
import com.example.realisticlighting.registry.ModItems;
import net.minecraft.core.BlockPos;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.Tag;
import com.example.realisticlighting.logic.LedExtendedLighting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import com.example.realisticlighting.menu.LampMenu;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.util.Mth;

public class LampBaseBlockEntity extends BlockEntity implements MenuProvider {
    // 0 = On, 1 = Off, 2 = Redstone
    private int operatingMode = 0;
    public int getOperatingMode() { return operatingMode; }
    public void setOperatingMode(int mode) {
        if (mode < 0 || mode > 2) return;
        operatingMode = mode; updateLitState(); setChanged(); sync();
    }
    public boolean isRedstonePowered() { return level != null && level.hasNeighborSignal(worldPosition); }
    private boolean enabled() { return operatingMode == 0 || (operatingMode == 2 && isRedstonePowered()); }
    public IEnergyStorage getEnergyStorage() {
    return energy;
}
	public int getEnergyStored() { return energy.getEnergyStored(); }
    public int getEnergyCapacity() { return energy.getMaxEnergyStored(); }
    public long getAgeTicks() { return ageTicks; }
    public int getEnergyCost() { return bulb.isEmpty() ? 0 : energyPerTick(); }
    public long getLifespanTicks() { return lifespan(); }
    @Override public Component getDisplayName() { return getBlockState().getBlock().getName(); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new LampMenu(id, inventory, this); }
    private ItemStack bulb = ItemStack.EMPTY;
    private long ageTicks = 0;
    private final InternalEnergyStorage energy;
    private static class InternalEnergyStorage extends EnergyStorage {
        InternalEnergyStorage(int capacity) { super(capacity, 100, 0); }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return 0; }
        // Internal drain bypasses the external extraction restriction.
        int consume(int amount) {
            int taken = Math.min(amount, energy);
            energy -= taken;
            return taken;
        }
        void restore(int amount) { energy = Math.max(0, Math.min(capacity, amount)); }
    }
    private boolean lastLit;
    private final Set<BlockPos> ledNodes = new HashSet<>();

    /** Reconcile periodically to handle changed obstacles and node replacement. */
    private void updateLedNodes(boolean active) {
        if (level == null || level.isClientSide || (!active && ledNodes.isEmpty())) return;
        Set<BlockPos> desired = active ? LedExtendedLighting.desired(level, worldPosition, getBlockState()) : Set.of();
        for (BlockPos old : new HashSet<>(ledNodes)) {
            if (desired.contains(old)) continue;
            if (!level.hasChunkAt(old)) continue; // Retry after that chunk loads.
            LedExtendedLighting.remove(level, worldPosition, Set.of(old));
            ledNodes.remove(old);
        }
        if (active) {
            LedExtendedLighting.add(level, worldPosition, getBlockState(), desired);
            ledNodes.addAll(desired);
        }
        setChanged();
    }
    public void clearLedNodes() { updateLedNodes(false); }
    public boolean ownsLedNode(BlockPos pos) { return ledNodes.contains(pos); }
    public boolean isExtendedLedActive() {
        return enabled() && bulb.is(ModItems.LED_BULB.get()) && ageTicks < lifespan()
            && energy.getEnergyStored() >= energyPerTick();
    }

    /** Internal wire injection; the ordinary FE capability remains receive-only. */
    protected void receiveWireEnergy(int amount) {
        if (amount > 0 && energy.receiveEnergy(amount, false) > 0) {
            setChanged();
            updateLitState();
        }
    }


    public LampBaseBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LAMP_BASE.get(), pos, state);
        int capacity = state.getBlock() instanceof SquareCeilingFixtureBlock ? EnergyConfig.ceiling()
            : state.getBlock() instanceof WallSconceBlock ? EnergyConfig.sconce()
            : EnergyConfig.cage();
        energy = new InternalEnergyStorage(capacity);
    }
    public ItemStack getBulb() { return bulb; }
  
private static final String BULB_AGE_KEY = "RealisticLightingAgeTicks";

public void setBulb(ItemStack stack) {
    bulb = stack.copyWithCount(1);

    if (bulb.isEmpty()) {
        ageTicks = 0;
    } else {
        CustomData data = bulb.getOrDefault(
                DataComponents.CUSTOM_DATA,
                CustomData.EMPTY
        );

        ageTicks = Mth.clamp(
                data.copyTag().getLong(BULB_AGE_KEY),
                0L,
                lifespan()
        );
    }

    updateLitState();
    setChanged();
    sync();
}

public ItemStack removeBulb() {
    ItemStack result = bulb.copy();

    if (!result.isEmpty()) {
        CompoundTag data = result.getOrDefault(
                DataComponents.CUSTOM_DATA,
                CustomData.EMPTY
        ).copyTag();

        data.putLong(BULB_AGE_KEY, ageTicks);

        result.set(
                DataComponents.CUSTOM_DATA,
                CustomData.of(data)
        );
    }

    bulb = ItemStack.EMPTY;
    ageTicks = 0;

    updateLitState();
    setChanged();
    sync();

    return result;
}


    private long lifespan() {
        if (bulb.is(ModItems.INCANDESCENT_BULB.get())) return 30L * 24_000L;
        if (bulb.is(ModItems.FLUORESCENT_BULB.get()) || bulb.is(ModItems.GROW_BULB.get())) return 90L * 24_000L;
        if (bulb.is(ModItems.LED_BULB.get())) return 300L * 24_000L;
        return 0;
    }
    private int energyPerTick() {
        if (bulb.is(ModItems.INCANDESCENT_BULB.get())) return EnergyConfig.incandescent();
        if (bulb.is(ModItems.FLUORESCENT_BULB.get())) return EnergyConfig.fluorescent();
        if (bulb.is(ModItems.GROW_BULB.get())) return EnergyConfig.grow();
        if (bulb.is(ModItems.LED_BULB.get())) return EnergyConfig.led();
        return Integer.MAX_VALUE;
    }
    public static void serverTick(Level level, BlockPos pos, BlockState state, LampBaseBlockEntity lamp) {
        if (level.isClientSide) return;
        boolean lit = lamp.enabled() && !lamp.bulb.isEmpty() && lamp.ageTicks < lamp.lifespan() && lamp.energy.getEnergyStored() >= lamp.energyPerTick();
        if (lit) { lamp.energy.consume(lamp.energyPerTick()); lamp.ageTicks++;
            if (lamp.bulb.is(ModItems.GROW_BULB.get()) && level.getGameTime() % 20 == 0)
                com.example.realisticlighting.logic.GrowLampGrowth.apply(level, pos);
        }
        if (lamp.lastLit != lit) { lamp.lastLit = lit; lamp.updateLitState(); lamp.sync(); }
        if (level.getGameTime() % 40 == 0) lamp.updateLedNodes(lit && lamp.bulb.is(ModItems.LED_BULB.get()));
        if (lit || level.getGameTime() % 100 == 0) lamp.setChanged();
    }
    public void refreshLighting() { updateLitState(); setChanged(); sync(); }
    private void updateLitState() {
        if (level == null || level.isClientSide) return;
        boolean shouldLight = enabled() && !bulb.isEmpty() && ageTicks < lifespan() && energy.getEnergyStored() >= energyPerTick();
        BlockState current = getBlockState();
        if (current.hasProperty(IndustrialFixtureBlock.BULB_KIND)) {
            int kind = bulb.is(ModItems.INCANDESCENT_BULB.get()) ? 1 : bulb.is(ModItems.FLUORESCENT_BULB.get()) ? 2 : bulb.is(ModItems.LED_BULB.get()) ? 3 : bulb.is(ModItems.GROW_BULB.get()) ? 4 : 0;
            if (current.getValue(IndustrialFixtureBlock.BULB_KIND) != kind) {
                current = current.setValue(IndustrialFixtureBlock.BULB_KIND, kind);
                level.setBlock(worldPosition, current, 3);
            }
        }
        if (current.hasProperty(LampBaseBlock.LIT) && current.getValue(LampBaseBlock.LIT) != shouldLight)
            level.setBlock(worldPosition, current.setValue(LampBaseBlock.LIT, shouldLight), 3);
        lastLit = shouldLight;
        updateLedNodes(shouldLight && bulb.is(ModItems.LED_BULB.get()));
    }
    private void sync() { if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3); }
    
@Override
protected void saveAdditional(
        CompoundTag tag,
        HolderLookup.Provider registries
) {
    super.saveAdditional(tag, registries);

    if (!bulb.isEmpty()) {
        tag.put("Bulb", bulb.save(registries));
    }

    tag.putLong("AgeTicks", ageTicks);
    tag.putInt("Energy", energy.getEnergyStored());
    tag.putInt("OperatingMode", operatingMode);

    long[] nodes = ledNodes.stream()
            .mapToLong(BlockPos::asLong)
            .toArray();

    tag.putLongArray("LedNodes", nodes);
}

@Override
protected void loadAdditional(
        CompoundTag tag,
        HolderLookup.Provider registries
) {
    super.loadAdditional(tag, registries);

    bulb = tag.contains("Bulb", Tag.TAG_COMPOUND)
            ? ItemStack.parseOptional(
                    registries,
                    tag.getCompound("Bulb")
              )
            : ItemStack.EMPTY;

    ageTicks = tag.getLong("AgeTicks");
    energy.restore(tag.getInt("Energy"));

    operatingMode = Math.max(
            0,
            Math.min(2, tag.getInt("OperatingMode"))
    );

    lastLit = false;

    ledNodes.clear();

    for (long packed : tag.getLongArray("LedNodes")) {
        ledNodes.add(BlockPos.of(packed));
    }
}

@Override
public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
    return saveWithoutMetadata(registries);
}

@Override
public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
}

}