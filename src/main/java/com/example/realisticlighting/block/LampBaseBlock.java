
package com.example.realisticlighting.block;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.example.realisticlighting.blockentity.LampBaseBlockEntity;
import com.example.realisticlighting.registry.ModBlockEntities;
import com.example.realisticlighting.registry.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

public class LampBaseBlock extends BaseEntityBlock {

	public static final MapCodec<LampBaseBlock> CODEC =
			simpleCodec(LampBaseBlock::new);

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
}

    public static final BooleanProperty LIT =
            BooleanProperty.create("lit");

    public LampBaseBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                stateDefinition.any().setValue(LIT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(LIT);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.createFixture(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return level.isClientSide
                ? null
                : createTickerHelper(
                        type,
                        ModBlockEntities.LAMP_BASE.get(),
                        LampBaseBlockEntity::serverTick
                );
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack held,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (hand != InteractionHand.MAIN_HAND) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!(level.getBlockEntity(pos) instanceof LampBaseBlockEntity lamp)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (player.isShiftKeyDown()) {
            removeInstalledBulb(level, player, lamp);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        if (isBulb(held)) {
            if (lamp.getBulb().isEmpty()) {
                if (!level.isClientSide) {
                    ItemStack installed = held.copyWithCount(1);
                    lamp.setBulb(installed);

                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hit
    ) {
        if (!(level.getBlockEntity(pos) instanceof LampBaseBlockEntity lamp)) {
            return InteractionResult.PASS;
        }

        if (player.isShiftKeyDown()) {
            removeInstalledBulb(level, player, lamp);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(lamp, pos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private static boolean isBulb(ItemStack stack) {
        return stack.is(ModItems.INCANDESCENT_BULB.get())
                || stack.is(ModItems.FLUORESCENT_BULB.get())
                || stack.is(ModItems.LED_BULB.get())
                || stack.is(ModItems.GROW_BULB.get());
    }

    private static void removeInstalledBulb(
            Level level,
            Player player,
            LampBaseBlockEntity lamp
    ) {
        if (level.isClientSide || lamp.getBulb().isEmpty()) {
            return;
        }

        ItemStack removed = lamp.removeBulb();

        if (!player.getInventory().add(removed)) {
            player.drop(removed, false);
        }
    }

    @Override
    protected void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            BlockPos fromPos,
            boolean moving
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, moving);

        if (!level.isClientSide
                && level.getBlockEntity(pos) instanceof LampBaseBlockEntity lamp) {
            lamp.refreshLighting();
        }
    }

  
@Override
protected void onRemove(
        BlockState state,
        Level level,
        BlockPos pos,
        BlockState newState,
        boolean moving
) {
    if (!state.is(newState.getBlock()) && !level.isClientSide) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof LampBaseBlockEntity lamp) {
            // Disconnect IE wires before removing the fixture.
            if (net.neoforged.fml.ModList.get()
                    .isLoaded("immersiveengineering")) {
                com.example.realisticlighting.compat.IEWireHooks.disconnect(lamp);
            }

            // Remove extended lighting nodes.
            lamp.clearLedNodes();

            // Return the installed bulb to the world.
            ItemStack bulb = lamp.removeBulb();

            if (!bulb.isEmpty()) {
                Block.popResource(level, pos, bulb);
            }
        }
    }

    super.onRemove(state, level, pos, newState, moving);
}
}