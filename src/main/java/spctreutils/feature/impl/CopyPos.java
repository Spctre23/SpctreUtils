package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.lwjgl.glfw.GLFW;
import spctreutils.feature.Feature;
import spctreutils.helper.*;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.List;

public class CopyPos extends Feature
{
    private static final Setting<Boolean> scaled = new Setting<>("Copy opposite dimension coords : CTRL", false, Boolean.class);

    private BlockPos renderPos = null;
    private Color color = Color.YELLOW;
    private final Delay unrenderDelay = new Delay();

    public CopyPos()
    {
        super("Copy Coords of Aimed Block", "Copies the coordinates of the block you are looking at to clipboard.", KEY_BEHAVIOR.TRIGGER, List.of(scaled));
    }

    @Override
    protected void onKeyPressed()
    {
        BlockPos blockPos = RaycastHelper.getAimedBlock();
        if (blockPos == null) return;
        renderPos = blockPos;

        boolean shouldScale = scaled.getValue() && Screen.hasControlDown();
        color = shouldScale ? getOppositeDimensionColor() : Color.YELLOW;
        blockPos = shouldScale ? DimensionHelper.getOppositePos(blockPos) : blockPos;

        String posString = blockPos.getX() + " " + renderPos.getY() + " " + blockPos.getZ();
        GLFW.glfwSetClipboardString(mc.getWindow().getWindow(), posString);
        Msg.sendHud("Copied position: " + posString, color);

        unrenderDelay.set(0.5);
    }

    @Override
    protected void onRender(WorldRenderContext context)
    {
        if (renderPos == null || unrenderDelay.isOver()) return;
        RenderHelper.drawOutline(context, renderPos, color, true);
    }

    @Override
    protected void onDisabled() { renderPos = null; }

    private Color getOppositeDimensionColor()
    {
        Minecraft mc = Minecraft.getInstance();
        ResourceKey dimension = mc.level.dimension();
        if (dimension == Level.NETHER) return Color.CYAN;
        if (dimension == Level.OVERWORLD) return Color.RED;
        return Color.YELLOW;
    }
}
