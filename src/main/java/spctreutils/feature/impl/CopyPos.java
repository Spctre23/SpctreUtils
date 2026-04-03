package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;
import spctreutils.feature.Feature;
import spctreutils.utils.Delay;
import spctreutils.utils.Msg;
import spctreutils.utils.RenderHelper;

public class CopyPos extends Feature
{
    private BlockPos copyPos = null;
    private final Delay unrenderDelay = new Delay();

    public CopyPos() {
        super("Copy Coords of Aimed Block", "Copies the coordinates of the block you are looking at to your clipboard.", false, false);

        WorldRenderEvents.AFTER_ENTITIES.register(context ->
        {
            if (copyPos == null || unrenderDelay.isOver()) return;
            RenderHelper.drawBlockOutline(context, copyPos, 1f, 1f, 0f, 1f);
        });
    }

    @Override
    public void onEnable()
    {
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.getPosition();
        Vec3 lookAngle = camera.getEntity().getLookAngle();
        double distance = 100;
        Vec3 raycastEnd = cameraPos.add(lookAngle.scale(distance));

        BlockHitResult hitResult = mc.level.clip(new ClipContext(
                cameraPos,
                raycastEnd,
                ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE,
                camera.getEntity()
        ));

        if (hitResult.getType() != BlockHitResult.Type.MISS)
        {
            copyPos = hitResult.getBlockPos();
            String posString = copyPos.getX() + " " + copyPos.getY() + " " + copyPos.getZ();
            GLFW.glfwSetClipboardString(mc.getWindow().getWindow(), posString);
            Msg.sendHud("Copied position: " + posString);
            unrenderDelay.set(0.5);
        }

        enabled = false;
    }

    @Override
    public void onDisable()
    {
        copyPos = null;
    }
}
