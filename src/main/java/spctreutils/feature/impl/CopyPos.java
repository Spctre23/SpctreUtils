package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.core.BlockPos;
import org.lwjgl.glfw.GLFW;
import spctreutils.feature.Feature;
import spctreutils.helper.*;
import spctreutils.setting.Setting;

import java.awt.*;

public class CopyPos extends Feature
{
    public static final Setting<Boolean> SCALED = new Setting<>(
            "Copy opposite dimension coords (CTRL)",
            true,
            Boolean.class
    );

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

        if (SCALED.getValue() == true)
            copyPos = DimensionHelper.getOppositePos(blockPos);
        else copyPos = blockPos;

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
