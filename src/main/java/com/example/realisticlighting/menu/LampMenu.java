package com.example.realisticlighting.menu;

import com.example.realisticlighting.blockentity.LampBaseBlockEntity;
import com.example.realisticlighting.registry.ModItems;
import com.example.realisticlighting.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LampMenu extends AbstractContainerMenu {
    private final BlockPos pos;
    private final LampBaseBlockEntity lamp;
    private final Container bulbInventory;
    private final ContainerData data;
    private boolean updatingSlot;

   
public LampMenu(
        int id,
        Inventory inventory,
        RegistryFriendlyByteBuf buffer
) {
    this(
            id,
            inventory,
            inventory.player.level().getBlockEntity(
                    buffer.readBlockPos()
            ) instanceof LampBaseBlockEntity be ? be : null
    );
}


    public LampMenu(int id, Inventory inventory, LampBaseBlockEntity lamp) {
        super(ModMenus.LAMP.get(), id);
        this.lamp = lamp;
        this.pos = lamp == null ? BlockPos.ZERO : lamp.getBlockPos();
        this.bulbInventory = new SimpleContainer(1) {
            @Override public void setChanged() {
                super.setChanged();
                if (LampMenu.this.lamp != null && !LampMenu.this.updatingSlot && !inventory.player.level().isClientSide) {
                    ItemStack stack = getItem(0);
                    if (stack.isEmpty()) LampMenu.this.lamp.removeBulb();
                    else LampMenu.this.lamp.setBulb(stack);
                }
            }
        };
        updatingSlot = true;
        if (lamp != null) bulbInventory.setItem(0, lamp.getBulb().copy());
        updatingSlot = false;
        this.data = inventory.player.level().isClientSide ? new SimpleContainerData(12) : new ContainerData() {
            @Override public int get(int index) {
                return switch (index) {
                    case 0 -> lamp.getEnergyStored() & 0xFFFF;
                    case 1 -> (lamp.getEnergyStored() >>> 16) & 0xFFFF;
                    case 2 -> (int) (lamp.getAgeTicks() & 0xFFFF);
                    case 3 -> (int) ((lamp.getAgeTicks() >>> 16) & 0xFFFF);
                    case 4 -> (int) (lamp.getLifespanTicks() & 0xFFFF);
                    case 5 -> (int) ((lamp.getLifespanTicks() >>> 16) & 0xFFFF);
                    case 6 -> lamp.getEnergyCost();
                    case 7 -> lamp.getOperatingMode();
                    case 8 -> lamp.isRedstonePowered() ? 1 : 0;
                    case 10 -> lamp.getEnergyCapacity() & 0xFFFF;
                    case 11 -> (lamp.getEnergyCapacity() >>> 16) & 0xFFFF;
                    case 9 -> lamp.getBlockState().getValue(com.example.realisticlighting.block.LampBaseBlock.LIT) ? 1 : 0;
                    default -> 0;
                };
            }
            @Override public void set(int index, int value) { }
            @Override public int getCount() { return 12; }
        };
        // The client must use writable storage: the server sends slot updates into it.
        // Reading directly from the client block entity (with a no-op set method)
        // leaves the GUI frozen until a block update or menu reopening.
        addDataSlots(data);

addSlot(new Slot(bulbInventory, 0, 149, 31) {
    @Override
    public boolean mayPlace(ItemStack stack) {
        return isBulb(stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public ItemStack remove(int amount) {
        if (amount <= 0 || getItem().isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!inventory.player.level().isClientSide && lamp != null) {
            // Capture the bulb's accumulated usage before it leaves
            // the GUI slot.
            ItemStack removed = lamp.removeBulb();

            // Keep the GUI inventory synchronized without triggering
            // another block-entity update.
            updatingSlot = true;
            try {
                bulbInventory.setItem(0, ItemStack.EMPTY);
            } finally {
                updatingSlot = false;
            }

            return removed;
        }

        return super.remove(amount);
    }
});

        for (int row = 0; row < 3; row++) for (int col = 0; col < 9; col++)
            addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 116 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 174));
    }

    private static boolean isBulb(ItemStack stack) {
        return stack.is(ModItems.INCANDESCENT_BULB.get()) || stack.is(ModItems.FLUORESCENT_BULB.get()) || stack.is(ModItems.LED_BULB.get()) || stack.is(ModItems.GROW_BULB.get());
    }
    private int combined(int low, int high) { return (data.get(low) & 0xFFFF) | ((data.get(high) & 0xFFFF) << 16); }
    public int capacity() { return combined(10, 11); }
    public int energy() { return combined(0, 1); }
    public int age() { return combined(2, 3); }
    public int lifespan() { return combined(4, 5); }
    public int cost() { return data.get(6); }
    public int mode() { return data.get(7); }
    public boolean redstone() { return data.get(8) == 1; }
    public boolean lit() { return data.get(9) == 1; }

    @Override public boolean clickMenuButton(Player player, int button) {
        if (button < 0 || button > 2 || !stillValid(player)) return false;
        if (!player.level().isClientSide && lamp != null) lamp.setOperatingMode(button);
        return true;
    }
    @Override public boolean stillValid(Player player) {
        return lamp != null && !lamp.isRemoved() && player.level().getBlockEntity(pos) == lamp && player.distanceToSqr(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5) <= 64;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        
if (index == 0) {
    if (lamp == null || player.level().isClientSide) {
        return ItemStack.EMPTY;
    }

    // Save the bulb's current lifespan before transferring it.
    ItemStack preservedBulb = lamp.getBulb().copy();

    if (preservedBulb.isEmpty()) {
        return ItemStack.EMPTY;
    }

    // Preserve the bulb's usage through the block entity.
    preservedBulb = lamp.removeBulb();

    // Move the lifespan-preserving item into the player's inventory.
    if (!moveItemStackTo(preservedBulb, 1, 37, true)) {
        // Inventory full: restore the bulb to the fixture.
        lamp.setBulb(preservedBulb);
        return ItemStack.EMPTY;
    }

    updatingSlot = true;
    try {
        bulbInventory.setItem(0, ItemStack.EMPTY);
    } finally {
        updatingSlot = false;
    }

    return ItemStack.EMPTY;
}
 else if (isBulb(stack) && !slots.get(0).hasItem()) {
            if (!moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
        } else if (index >= 1 && index < 28) {
            if (!moveItemStackTo(stack, 28, 37, false)) return ItemStack.EMPTY;
        } else if (index >= 28 && index < 37) {
            if (!moveItemStackTo(stack, 1, 28, false)) return ItemStack.EMPTY;
        } else return ItemStack.EMPTY;
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return original;
    }
}
