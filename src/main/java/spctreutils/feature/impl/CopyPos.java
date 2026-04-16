package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.core.BlockPos;
import org.lwjgl.glfw.GLFW;
import spctreutils.feature.Feature;
import spctreutils.util.Delay;
import spctreutils.util.Msg;
import spctreutils.util.RaycastHelper;
import spctreutils.util.RenderHelper;

import java.awt.*;

public class CopyPos extends Feature
{
    private BlockPos copyPos = null;
    private final Delay unrenderDelay = new Delay();

    public CopyPos()
    {
        super("Copy Coords of Aimed Block", "Copies the coordinates of the block you are looking at to clipboard.", KEY_BEHAVIOR.TRIGGER);

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context ->
        {
            if (copyPos == null || unrenderDelay.isOver()) return;
            RenderHelper.drawOutline(context, copyPos, 1f, 1f, 0f, 1f, true);
        });
    }

    @Override
    public void onKeyPressed()
    {
        BlockPos blockPos = RaycastHelper.getAimedBlock();
        if (blockPos == null) return;

        copyPos = blockPos;
        String posString = copyPos.getX() + " " + copyPos.getY() + " " + copyPos.getZ();
        GLFW.glfwSetClipboardString(mc.getWindow().getWindow(), posString);
        Msg.sendHud("Copied position: " + posString, Color.YELLOW);
        unrenderDelay.set(0.5);
    }

    @Override
    public void onDisabled()
    {
        copyPos = null;
    }
}
