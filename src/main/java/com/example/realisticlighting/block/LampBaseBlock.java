package com.example.realisticlighting.block;

import com.example.realisticlighting.blockentity.LampBaseBlockEntity;
import com.example.realisticlighting.registry.ModBlockEntities;
import com.example.realisticlighting.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class LampBaseBlock extends BaseEntityBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public LampBaseBlock(Properties properties) { super(properties); registerDefaultState(stateDefinition.any().setValue(LIT, false)); }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(LIT); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return ModBlockEntities.createFixture(pos, state); }
    @Nullable @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.LAMP_BASE.get(), LampBaseBlockEntity::serverTick);
    }
    @Override public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        BlockEntity raw = level.getBlockEntity(pos);
        if (!(raw instanceof LampBaseBlockEntity lamp)) return InteractionResult.PASS;
        ItemStack held = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && !lamp.getBulb().isEmpty()) {
                ItemStack removed = lamp.removeBulb();
                if (!player.getInventory().add(removed)) player.drop(removed, false);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (held.is(ModItems.INCANDESCENT_BULB.get()) || held.is(ModItems.FLUORESCENT_BULB.get()) || held.is(ModItems.LED_BULB.get()) || held.is(ModItems.GROW_BULB.get())) {
            if (!level.isClientSide && lamp.getBulb().isEmpty()) {
                ItemStack installed = held.copy(); installed.setCount(1); lamp.setBulb(installed);
                if (!player.getAbilities().instabuild) held.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, lamp, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        super.neighborChanged(state, level, pos, block, fromPos, moving);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof LampBaseBlockEntity lamp) lamp.refreshLighting();
    }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof LampBaseBlockEntity lamp && !level.isClientSide) {
                if (net.minecraftforge.fml.ModList.get().isLoaded("immersiveengineering"))
                    com.example.realisticlighting.compat.IEWireHooks.disconnect(lamp);
                lamp.clearLedNodes();
                ItemStack bulb = lamp.removeBulb(); if (!bulb.isEmpty()) Block.popResource(level, pos, bulb);
            }
        }
        super.onRemove(state, level, pos, newState, moving);
    }
}
