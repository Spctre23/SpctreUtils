package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import spctreutils.config.ConfigManager;
import spctreutils.feature.Feature;
import spctreutils.mixin.accessor.AzimuthWaypointInvoker;
import spctreutils.mixin.accessor.ChunkWaypointInvoker;
import spctreutils.mixin.accessor.Vec3iWaypointInvoker;
import spctreutils.util.Delay;
import spctreutils.util.Msg;
import spctreutils.util.RaycastHelper;
import spctreutils.util.RenderHelper;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public class GetEntitiesInBlock extends Feature
{
    private BlockPos copyPos = null;
    private final Delay unrenderDelay = new Delay();

    public GetEntitiesInBlock()
    {
        super("Get Entities in Block",
            "test",
            KEY_BEHAVIOR.TRIGGER,
            config -> config.getEntitiesInBlock,
            value -> ConfigManager.config.getEntitiesInBlock = value);

        WorldRenderEvents.AFTER_ENTITIES.register(context ->
        {
            if (copyPos == null || unrenderDelay.isOver()) return;
            RenderHelper.drawBlockOutline(context, copyPos, 1f, 1f, 0f, 1f);
        });
    }

    @Override
    public void onKeyPressed()
    {
        BlockPos block = RaycastHelper.getAimedBlock();
        copyPos = block;
        AABB aabb = AABB.ofSize(new Vec3(block.getX(), block.getY(), block.getZ()), 1,1,1);
        List<LivingEntity> entities = mc.level.getEntitiesOfClass(LivingEntity.class, aabb);

        StringBuilder sb = new StringBuilder();
        sb.append(entities.size());
        for (LivingEntity entity : entities)
        {
            sb.append("\n   - " + entity.getName().getString());
        }
        Msg.sendChat(sb.toString());
        unrenderDelay.set(0.5);
    }
}
