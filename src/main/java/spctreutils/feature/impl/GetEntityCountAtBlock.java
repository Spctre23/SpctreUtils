package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import spctreutils.feature.Feature;
import spctreutils.helper.Delay;
import spctreutils.helper.Msg;
import spctreutils.helper.RaycastHelper;
import spctreutils.helper.RenderHelper;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GetEntityCountAtBlock extends Feature
{
    private BlockPos block = null;
    private final Delay unrenderDelay = new Delay();

    public GetEntityCountAtBlock()
    {
        super("Get Entity Count at Block", "Prints number of entities at the block you are looking at.", KEY_BEHAVIOR.TRIGGER);
    }

    @Override
    protected void onKeyPressed()
    {
        block = RaycastHelper.getAimedBlock(true);
        if (block == null) return;

        AABB aabb = AABB.ofSize(new Vec3(block.getX(), block.getY(), block.getZ()), 1,1,1);
        List<LivingEntity> entities = mc.level.getEntitiesOfClass(LivingEntity.class, aabb);
        Map<String, Integer> entityCounts = new LinkedHashMap<>();
        for (LivingEntity entity : entities)
        {
            String name = entity.getType().getDescription().getString();
            entityCounts.merge(name, 1, Integer::sum);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Entities: " + entities.size());
        for (Map.Entry<String, Integer> entry : entityCounts.entrySet())
            sb.append("\n• " + entry.getKey() + ": " + entry.getValue());

        Msg.sendChat(sb.toString());
        unrenderDelay.set(0.5);
    }

    @Override
    protected void onRender(WorldRenderContext context)
    {
        if (block == null || unrenderDelay.isOver()) return;
        RenderHelper.drawOutline(context, block, Color.YELLOW, true);
    }
}
