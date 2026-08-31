package spctreutils.module.feature.impl;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
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
import spctreutils.helper.World.WorldEditHelper;
import spctreutils.key.Keybind;
import spctreutils.module.feature.ToggleFeature;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.List;

public class EasyBuild extends ToggleFeature
{
    private static final Setting<Boolean> MIRROR_BLOCK_STATE = new Setting<>("Mirror start pos block state", true, Boolean.class);

    private final Color lineColor = Color.GREEN;
    private final Color boxColor = Color.CYAN;

    private final Keybind drawModifierBind = new Keybind("Easy Build - Draw Modifier", InputConstants.KEY_LALT);
    private final Keybind switchShapeBind = new Keybind("Easy Build - Switch Shape", InputConstants.MOUSE_BUTTON_4);
    private final Keybind undoModifierBind = new Keybind("Easy Build - Undo Modifier", InputConstants.KEY_LCONTROL);
    private final Keybind undoTriggerBind = new Keybind("Easy Build - Undo Trigger", InputConstants.KEY_Z);
    private final Keybind redoModifierBind = new Keybind("Easy Build - Redo Modifier", InputConstants.KEY_LCONTROL);
    private final Keybind redoTriggerBind = new Keybind("Easy Build - Redo Trigger", InputConstants.KEY_Y);
    private final Keybind copyModifierBind = new Keybind("Easy Build - Copy Modifier", InputConstants.KEY_LCONTROL);
    private final Keybind copyTriggerBind = new Keybind("Easy Build - Copy Trigger", InputConstants.KEY_Y);

    private BlockHitResult startHit;
    private DragSession session;
    private AABB previewBox;
    private DragShape shape = DragShape.BOX;
    private int shapeIndex;
    private boolean isCopying = false;

    public EasyBuild()
    {
        super("Easy Build", """
                Allows you to effortlessly draw and fill an area with blocks.
                - Hold the DRAW MODIFIER bind to draw.
                - PLACE to fill the volume. BREAK to delete everything within the volume.
                - Hold the UNDO MODIFIER and press the UNDO TRIGGER to undo the last action. Redoing follows the same logic. ONLY works if WorldEdit is present.
                
                There are two shape modes, LINE and PLANE.
                - When in PLANE mode, you can extend its depth to create a BOX by scrolling the mouse wheel.
                - Cycle between shape modes with the SWITCH SHAPE modifier.""",
            List.of(MIRROR_BLOCK_STATE));

        registerSwitchShapeBind();
        registerUndoBind();
        registerRedoBind();
        FillDispatcher.init();
    }

    @Override
    protected void onTick()
    {
        GameType gameType = mc.player.gameMode();
        if (!drawModifierBind.isDown() || gameType == null || gameType.isSurvival())
        {
            session = null;
            previewBox = null;
            startHit = null;
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
        if (!drawModifierBind.isDown() || session == null || shape != DragShape.BOX)
            return InteractionResult.PASS;

        session.adjustDepth(delta > 0 ? 1 : -1);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRender(LevelRenderContext context)
    {
        if (session == null || previewBox == null || !drawModifierBind.isDown()) return;

        RenderHelper.drawOutline(context, previewBox, getShapeColor(shape));
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
                if (MIRROR_BLOCK_STATE.getValue())
                {
                    BlockState startBlockState = mc.level.getBlockState(startHit.getBlockPos());
                    if (blockState.getProperties().equals(startBlockState.getProperties()))
                    {
                        blockState = heldBlock.withPropertiesOf(startBlockState);
                    }
                }

                if (WorldEditHelper.fillArea(previewBox, blockState)) return;
                FillDispatcher.queueFill(previewBox, blockState);
                return;
            }
        }

        while (mc.options.keyAttack.consumeClick())
        {
            BlockState blockState = Blocks.AIR.defaultBlockState();
            if (WorldEditHelper.fillArea(previewBox, blockState)) return;
            FillDispatcher.queueFill(previewBox, blockState);
        }
    }

    private void registerSwitchShapeBind()
    {
        switchShapeBind.onPressed(() ->
        {
            GameType gameType = mc.player.gameMode();
            if (gameType == null || gameType.isSurvival()) return;

            shapeIndex = (shapeIndex + 1) % DragShape.values().length;
            shape = DragShape.values()[shapeIndex];

            Component hudMessage = Component.literal("Draw Shape = ").append(shape.name()).withColor(getShapeColor(shape).getRGB());

            Msg.sendHud(hudMessage);
        });
    }

    public void registerUndoBind()
    {
        undoTriggerBind.onPressed(() ->
        {
            if (!undoModifierBind.isDown()) return;
            if (!WorldEditHelper.undo())
            {
                Msg.sendHud("Undo requires WorldEdit.", Color.RED);
            }
        });
    }

    public void registerRedoBind()
    {
        redoTriggerBind.onPressed(() ->
        {
            if (!redoModifierBind.isDown()) return;
            if (!WorldEditHelper.redo())
            {
                Msg.sendHud("Redo requires WorldEdit.", Color.RED);
            }
        });
    }

    private Color getShapeColor(DragShape shape)
    {
        return switch (shape)
        {
            case LINE -> lineColor;
            case BOX -> boxColor;
        };
    }
}