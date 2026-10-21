package com.example.realisticlighting.block;
import com.mojang.serialization.MapCodec;
import com.example.realisticlighting.blockentity.LedLightNodeBlockEntity;
import com.example.realisticlighting.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/** Invisible, non-colliding, replaceable light node. Never distributed as an item. */
public class LedLightNodeBlock extends BaseEntityBlock {
public static final MapCodec<LedLightNodeBlock> CODEC =
        simpleCodec(LedLightNodeBlock::new);

@Override
protected MapCodec<? extends BaseEntityBlock> codec() {
    return CODEC;
}   
   public static final IntegerProperty LIGHT = IntegerProperty.create("light", 1, 15);
    public LedLightNodeBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(LIGHT, 1));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) { builder.add(LIGHT); }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.INVISIBLE; }
    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    @Override public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return Shapes.empty(); }
    @Override public boolean canBeReplaced(BlockState state, net.minecraft.world.item.context.BlockPlaceContext context) { return true; }
    @Nullable @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.LED_LIGHT_NODE.get(), LedLightNodeBlockEntity::serverTick);
    }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new LedLightNodeBlockEntity(pos, state); }
}
