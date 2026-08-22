package spctreutils.module.feature.impl;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import spctreutils.helper.Drawing.DragSession;
import spctreutils.helper.Drawing.DragShape;
import spctreutils.helper.Visual.Msg;
import spctreutils.helper.Visual.RenderHelper;
import spctreutils.helper.World.FillDispatcher;
import spctreutils.helper.World.RaycastHelper;
import spctreutils.key.Keybind;
import spctreutils.module.feature.Feature;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.List;

public class EasyBuild extends Feature
{
    private static final Setting<Boolean> mirrorBlockState = new Setting<>("Mirror start pos block state", true, Boolean.class);

    private final Color LINE_COLOR = Color.GREEN;
    private final Color BOX_COLOR = Color.CYAN;
    private final Keybind DRAW_MODIFIER = new Keybind("Easy Build - Draw Modifier", InputConstants.KEY_LALT);
    private final Keybind SWITCH_SHAPE = new Keybind("Easy Build - Switch Shape", InputConstants.MOUSE_BUTTON_4);

    private BlockHitResult startHit;
    private DragSession session;
    private AABB previewBox;
    private DragShape shape = DragShape.BOX;
    private int shapeIndex;

    public EasyBuild()
    {
        super("Easy Build", """
                Allows you to effortlessly draw and fill an area with blocks.
                - Hold the DRAW MODIFIER bind to draw.
                - PLACE to fill the volume. BREAK to delete everything within the volume.
                
                There are two shape modes, LINE and PLANE.
                - When in PLANE mode, you can extend its depth to create a BOX by scrolling the mouse wheel.
                - Cycle between shape modes with the SWITCH SHAPE modifier.""",
            KEY_BEHAVIOR.TOGGLE,
            List.of(mirrorBlockState));

        registerModifierBind();
        FillDispatcher.init();
    }

    @Override
    protected void onTick()
    {
        if (!DRAW_MODIFIER.isDown())
        {
            session = null;
            previewBox = null;
            return;
        }

        Vector3fc forward = mc.gameRenderer.mainCamera().forwardVector();
        Vec3 look = new Vec3(forward.x(), forward.y(), forward.z());

        if (session == null)
        {
            startHit = RaycastHelper.getBlockHitResult();
            session = DragSession.begin(startHit.getBlockPos(), startHit.getDirection());
        }

        AABB box = session.currentBox(mc.gameRenderer.mainCamera().position(), look, shape);
        if (box != null) previewBox = box;

        handleFillConfirmation();
    }

    @Override
    protected InteractionResult onMouseScrolled(double delta)
    {
        if (!DRAW_MODIFIER.isDown() || session == null || shape != DragShape.BOX)
            return InteractionResult.PASS;

        session.adjustDepth(delta > 0 ? 1 : -1);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRender(LevelRenderContext context)
    {
        if (session == null || previewBox == null || !DRAW_MODIFIER.isDown()) return;

        RenderHelper.drawOutline(context, previewBox, getShapeColor(shape));
    }

    private void registerModifierBind()
    {
        SWITCH_SHAPE.onPressed(() ->
        {
            shapeIndex = (shapeIndex + 1) % DragShape.values().length;
            shape = DragShape.values()[shapeIndex];

            Component hudMessage = Component.literal("Draw Shape = ").append(shape.name()).withColor(getShapeColor(shape).getRGB());

            Msg.sendHud(hudMessage);
        });
    }

    private void handleFillConfirmation()
    {
        if (session == null || previewBox == null) return;

        while (mc.options.keyUse.consumeClick())
        {
            ItemStack mainHandItem = mc.player.getMainHandItem();
            if (mainHandItem.getItem() instanceof BlockItem blockItem)
            {
                if (startHit == null) return;

                Block heldBlock = blockItem.getBlock();
                BlockState blockState = heldBlock.defaultBlockState();
                if (mirrorBlockState.getValue())
                {
                    BlockState startBlockState = mc.level.getBlockState(startHit.getBlockPos());
                    if (blockState.getProperties().equals(startBlockState.getProperties()))
                    {
                        blockState = heldBlock.withPropertiesOf(startBlockState);
                    }
                }

                FillDispatcher.queueFill(previewBox, blockState);
            }
        }

        while (mc.options.keyAttack.consumeClick())
        {
            FillDispatcher.queueFill(previewBox, Blocks.AIR.defaultBlockState());
        }
    }

    private Color getShapeColor(DragShape shape)
    {
        return switch (shape)
        {
            case LINE -> LINE_COLOR;
            case BOX -> BOX_COLOR;
        };
    }
}