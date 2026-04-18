package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import spctreutils.feature.Feature;
import spctreutils.helper.*;

import java.awt.*;

public class GetDistanceToAimedBlock extends Feature
{
    private BlockPos renderPos = null;
    private final Delay unrenderDelay = new Delay();

    public GetDistanceToAimedBlock()
    {
        super("Get Distance to Aimed Block", "Prints the distance along each axis to the block you are looking at.", KEY_BEHAVIOR.TRIGGER);
    }

    @Override
    protected void onKeyPressed()
    {
        BlockPos blockPos = RaycastHelper.getAimedBlock();
        if (blockPos == null) return;
        renderPos = blockPos;

        double totalDistance = mc.player.position().distanceTo(Vec3.atCenterOf(blockPos));
        double distanceX = Math.abs(blockPos.getX() - mc.player.getX());
        double distanceY = Math.abs(blockPos.getY() - mc.player.getY());
        double distanceZ = Math.abs(blockPos.getZ() - mc.player.getZ());

        Msg.sendChat(String.format("Total distance: %.2f", totalDistance),
            String.format("X: %.0f", distanceX),
            String.format("Y: %.0f", distanceY),
            String.format("Z: %.0f", distanceZ));

        unrenderDelay.set(0.5);
    }

    @Override
    protected void onRender(WorldRenderContext context)
    {
        if (renderPos == null || unrenderDelay.isOver()) return;
        RenderHelper.drawOutline(context, renderPos, Color.YELLOW, true);
    }

    @Override
    protected void onDisabled() { renderPos = null; }
}
