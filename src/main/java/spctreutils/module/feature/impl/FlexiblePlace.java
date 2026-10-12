package spctreutils.module.feature.impl;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import spctreutils.helper.Visual.Msg;
import spctreutils.helper.Visual.RenderHelper;
import spctreutils.helper.World.PlacementHelper;
import spctreutils.key.Keybind;
import spctreutils.module.feature.ToggleFeature;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.Optional;

public class FlexiblePlace extends ToggleFeature
{
    private static final Setting<Float> BLOCK_PREVIEW_OPACITY = new Setting<>("Block placement preview opacity", 0.5f, Float.class);

    //private static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    //private static final EnumProperty<SlabType> SLAB_TYPE = BlockStateProperties.SLAB_TYPE;

    public final Keybind flexiblePlaceModifier = new Keybind("Flexible Place - Modifier", InputConstants.KEY_LCONTROL);
    private final Keybind switchPropertyBind = new Keybind("Flexible Place - Switch Property", InputConstants.MOUSE_BUTTON_4);

    public BlockState blockState;
    private BlockPos blockPos;
    private Property<?> property = null;
    private int propertyIndex;

/*    enum BlockStateProperty
    {
        ROTATION,
        HALF
    }*/

    public FlexiblePlace()
    {
        super("Flexible Place", "");

        registerSwitchPropertyBind();
    }

    @Override
    protected void onTick()
    {
        if (!flexiblePlaceModifier.isDown()) return;

        ItemStack stack = mc.player.getMainHandItem();
        if (!(stack.getItem() instanceof BlockItem blockItem) ||
            !(mc.hitResult instanceof BlockHitResult hit) ||
            !PlacementHelper.canPlaceHeldBlock(stack, blockItem, hit))
        {
            blockPos = null;
            blockState = null;
            return;
        }

        blockPos = hit.getBlockPos().relative(hit.getDirection());
        Block heldBlock = blockItem.getBlock();

        if (blockState != null && blockState.getBlock() == heldBlock) return;
        blockState = heldBlock.defaultBlockState();
        Optional<Property<?>> first = blockState.getProperties().stream().findFirst();
        first.ifPresent(value -> property = value);
    }

    @Override
    protected InteractionResult onMouseScrolled(double delta)
    {
        if (!flexiblePlaceModifier.isDown() || blockState == null || property == null)
            return InteractionResult.PASS;

        blockState = blockState.cycle(property);
/*        switch(blockState.getProperties())
        {
            case 1 ->
            {
                EnumProperty<Direction> facing = BlockStateProperties.FACING;
                blockState = blockState.cycle(facing);
            }
            case HALF ->
            {
                EnumProperty<Half> half = BlockStateProperties.HALF;
                EnumProperty<SlabType> slabType = BlockStateProperties.SLAB_TYPE;
                if (blockState.hasProperty(half))
                    blockState = blockState.cycle(half);
                else if (blockState.hasProperty(slabType))
                    blockState = blockState.cycle(slabType);
            }
        }*/
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRender(LevelRenderContext context)
    {
        if (blockState == null || !flexiblePlaceModifier.isDown()) return;
        RenderHelper.drawBlock(context, blockPos, blockState, BLOCK_PREVIEW_OPACITY.getValue());
    }

    private void registerSwitchPropertyBind()
    {
        switchPropertyBind.onPressed(() ->
        {
            if (!enabled || blockState == null) return;

            propertyIndex = (propertyIndex + 1) % blockState.getProperties().size();
            property = (Property<?>) blockState.getProperties().toArray()[propertyIndex];

            Component hudMessage = Component.literal(String.format("Modifying property: %s", property.getName())).withColor(Color.GREEN.getRGB());
            Msg.sendHud(hudMessage);
        });
    }
}