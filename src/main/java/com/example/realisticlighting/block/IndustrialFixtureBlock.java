package com.example.realisticlighting.block;

import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

/** Six-way mountable industrial cage fixture. Bulb appearance is blockstate-driven. */
public class IndustrialFixtureBlock extends LampBaseBlock {
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    // 0 empty, 1 incandescent, 2 fluorescent, 3 LED, 4 Grow Lamp
    public static final IntegerProperty BULB_KIND = IntegerProperty.create("bulb_kind", 0, 4);

    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

    public IndustrialFixtureBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP).setValue(BULB_KIND, 0).setValue(COLOR, DyeColor.GRAY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, BULB_KIND, COLOR);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand == InteractionHand.MAIN_HAND && !player.isShiftKeyDown() && player.getItemInHand(hand).getItem() instanceof DyeItem dye) {
            DyeColor requested = dye.getDyeColor();
            if (state.getValue(COLOR) != requested) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(COLOR, requested), 3);
                    if (!player.getAbilities().instabuild) player.getItemInHand(hand).shrink(1);
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    /** Pick-block keeps the chosen paint color. */
    @Override
    public ItemStack getCloneItemStack(net.minecraft.world.level.BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(this);
        if (state.getValue(COLOR) != DyeColor.GRAY) {
            CompoundTag blockStateTag = new CompoundTag();
            blockStateTag.putString("color", state.getValue(COLOR).getName());
            stack.getOrCreateTag().put("BlockStateTag", blockStateTag);
        }
        return stack;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }
}
