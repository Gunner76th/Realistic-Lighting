package com.example.realisticlighting.blockentity;
import net.minecraft.core.HolderLookup;
import com.example.realisticlighting.block.LedLightNodeBlock;
import com.example.realisticlighting.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.HashMap;
import java.util.Map;

/** Multiple LED fixtures may own the same light node. Persisted ownership prevents cross-fixture cleanup. */
public class LedLightNodeBlockEntity extends BlockEntity {
    private final Map<Long, Integer> owners = new HashMap<>();
    public LedLightNodeBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.LED_LIGHT_NODE.get(), pos, state); }
    /** Prune abandoned owners after fixture removal, including when chunks load in a different order. */
    public static void serverTick(Level level, BlockPos pos, BlockState state, LedLightNodeBlockEntity node) {
        if (level.getGameTime() % 100 != 0) return;
        for (long packed : new java.util.HashSet<>(node.owners.keySet())) {
            BlockPos fixture = BlockPos.of(packed);
            if (!level.hasChunkAt(fixture)) continue;
            if (!(level.getBlockEntity(fixture) instanceof LampBaseBlockEntity lamp) || (!lamp.isExtendedLedActive() || !lamp.ownsLedNode(pos))) {
                node.removeOwner(fixture);
                if (node.isRemoved()) return;
            }
        }
    }
    public void addOwner(BlockPos fixture, int light) {
        owners.put(fixture.asLong(), light);
        refresh();
    }
    public void removeOwner(BlockPos fixture) {
        if (owners.remove(fixture.asLong()) != null) refresh();
    }
    private void refresh() {
        if (level == null || level.isClientSide) return;
        if (owners.isEmpty()) {
            level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), 3);
        } else {
            int strength = owners.values().stream().mapToInt(Integer::intValue).max().orElse(1);
            BlockState current = getBlockState();
            if (current.getValue(LedLightNodeBlock.LIGHT) != strength)
                level.setBlock(worldPosition, current.setValue(LedLightNodeBlock.LIGHT, strength), 3);
            setChanged();
        }
    }

    @Override
    protected void saveAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.saveAdditional(tag, registries);

        ListTag list = new ListTag();

        owners.forEach((pos, light) -> {
            CompoundTag entry = new CompoundTag();
            entry.putLong("Fixture", pos);
            entry.putInt("Light", light);
            list.add(entry);
        });

        tag.put("Owners", list);
    }

    @Override
    protected void loadAdditional(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        super.loadAdditional(tag, registries);

        owners.clear();

        ListTag list = tag.getList(
                "Owners",
                Tag.TAG_COMPOUND
        );

        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);

            owners.put(
                    entry.getLong("Fixture"),
                    Math.max(
                            1,
                            Math.min(15, entry.getInt("Light"))
                    )
            );
        }
    }
}