package spctreutils.feature.impl;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.core.BlockPos;
import org.lwjgl.glfw.GLFW;
import spctreutils.config.ConfigManager;
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
        super("Copy Coords of Aimed Block",
            KEY_BEHAVIOR.TRIGGER,
            config -> config.feature.copyPos,
            value -> ConfigManager.config.feature.copyPos = value);

        WorldRenderEvents.AFTER_ENTITIES.register(context ->
        {
            if (copyPos == null || unrenderDelay.isOver()) return;
            RenderHelper.drawBlockOutline(context, copyPos, 1f, 1f, 0f, 1f);
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
