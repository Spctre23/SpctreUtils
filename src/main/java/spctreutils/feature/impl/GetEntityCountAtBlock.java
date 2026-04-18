package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import spctreutils.feature.Feature;
import spctreutils.helper.*;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
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

        AABB aabb = new AABB(block);
        HashSet<Entity> entities = EntityHelper.getEntities(aabb);
        Map<String, Integer> entityCounts = new LinkedHashMap<>();
        for (Entity entity : entities)
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
