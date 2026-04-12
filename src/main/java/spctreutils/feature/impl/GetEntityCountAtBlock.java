package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import spctreutils.config.ConfigManager;
import spctreutils.feature.Feature;
import spctreutils.util.Delay;
import spctreutils.util.Msg;
import spctreutils.util.RaycastHelper;
import spctreutils.util.RenderHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GetEntityCountAtBlock extends Feature
{
    private BlockPos block = null;
    private final Delay unrenderDelay = new Delay();

    public GetEntityCountAtBlock()
    {
        super("Get Entity Count at Block",
            KEY_BEHAVIOR.TRIGGER,
            config -> config.feature.getEntityCountAtBlock,
            value -> ConfigManager.config.feature.getEntityCountAtBlock = value);

        WorldRenderEvents.AFTER_ENTITIES.register(context ->
        {
            if (block == null || unrenderDelay.isOver()) return;
            RenderHelper.drawBlockOutline(context, block, 1f, 1f, 0f, 1f);
        });
    }

    @Override
    public void onKeyPressed()
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
}
