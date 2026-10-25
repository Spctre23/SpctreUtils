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
import spctreutils.helper.Placement.PlacementHelper;
import spctreutils.key.Keybind;
import spctreutils.module.feature.ToggleFeature;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.List;

public class FlexiblePlace extends ToggleFeature
{
    private static final Setting<Float> BLOCK_PREVIEW_OPACITY = new Setting<>("Block placement preview opacity", 0.5f, Float.class);

    public final Keybind flexiblePlaceModifier = new Keybind("Flexible Place - Modifier", InputConstants.KEY_LCONTROL);
    private final Keybind switchPropertyBind = new Keybind("Flexible Place - Switch Property", InputConstants.MOUSE_BUTTON_4);

    public BlockState blockState;
    private BlockPos blockPos;
    private int propertyIndex;
    private List<Property<?>> properties = List.of();
    private boolean active;

    public FlexiblePlace()
    {
        super("Flexible Place", """
            Allows you to effortlessly control properties of blocks on placement (EG: slab shape or stair rotation).
            - Hold your MODIFIER + scroll to cycle the state of the currently selected property.
            - Press your PROPERTY SWITCH bind to switch the property you want to modify.""");

        registerSwitchPropertyBind();
    }

    @Override
    protected void onTick()
    {
        active = false;
        if (!flexiblePlaceModifier.isDown()) return;

        ItemStack stack = mc.player.getMainHandItem();
        if (!(stack.getItem() instanceof BlockItem blockItem) ||
            !(mc.hitResult instanceof BlockHitResult hit) ||
            !PlacementHelper.canPlaceHeldBlock(stack, blockItem, hit))
            return;

        blockPos = hit.getBlockPos().relative(hit.getDirection());
        Block heldBlock = blockItem.getBlock();

        if (blockState == null || blockState.getBlock() != heldBlock)
            selectBlock(heldBlock);
        active = true;
    }

    @Override
    protected InteractionResult onMouseScrolled(double delta)
    {
        Property<?> property = currentProperty();
        if (!flexiblePlaceModifier.isDown() || !active || blockState == null || property == null)
            return InteractionResult.PASS;

        blockState = blockState.cycle(property);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRender(LevelRenderContext context)
    {
        if (!flexiblePlaceModifier.isDown() || !active || blockState == null) return;
        RenderHelper.drawBlock(context, blockPos, blockState, BLOCK_PREVIEW_OPACITY.getValue());
    }

    public BlockState getOverrideState(Block heldBlock)
    {
        if (!isEnabled() || !flexiblePlaceModifier.isDown() || blockState == null || blockState.getBlock() != heldBlock) return null;
        return blockState;
    }

    private void selectBlock(Block block)
    {
        blockState = block.defaultBlockState();
        properties = mc.getSingleplayerServer() == null
            ? blockState.getProperties().stream().filter(PlacementHelper::isSupported).toList()
            : List.copyOf(blockState.getProperties());
        propertyIndex = 0;
    }

    private void registerSwitchPropertyBind()
    {
        switchPropertyBind.onPressed(() ->
        {
            if (!isEnabled() || !active || properties.isEmpty()) return;

            propertyIndex = (propertyIndex + 1) % properties.size();
            Property<?> property = currentProperty();
            if (property == null) return;

            Msg.sendHud(Component.literal("Modifying property: " + property.getName()).withColor(Color.GREEN.getRGB()));
        });
    }

    private Property<?> currentProperty()
    {
        return properties.isEmpty() ? null : properties.get(propertyIndex);
    }
}