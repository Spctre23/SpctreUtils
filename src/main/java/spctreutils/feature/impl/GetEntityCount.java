package spctreutils.feature.impl;

import dev.isxander.yacl3.gui.utils.KeyUtils;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import spctreutils.feature.Feature;
import spctreutils.helper.*;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class GetEntityCount extends Feature
{
    private BlockPos block = null;
    private final Delay unrenderDelay = new Delay();

    public GetEntityCount()
    {
        super("Get Entity Count", "Prints number of entities in simulation distance (default), or within the block you are looking at if holding CTRL on activation.", KEY_BEHAVIOR.TRIGGER);
    }

    @Override
    protected void onKeyPressed()
    {
        HashSet<Entity> entities;
        boolean getAtBlock = KeyUtils.hasControlDown();
        if (getAtBlock)
        {
            block = RaycastHelper.getAimedBlock(true);
            if (block == null) return;
            entities = EntityHelper.getEntities(new AABB(block));
        }
        else
        {
            block = null;
            entities = EntityHelper.getEntities();
        }

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
