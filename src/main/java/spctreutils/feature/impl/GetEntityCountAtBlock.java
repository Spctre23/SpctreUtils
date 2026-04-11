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

import java.util.List;

public class GetEntityCountAtBlock extends Feature
{
    private BlockPos block = null;
    private final Delay unrenderDelay = new Delay();

    public GetEntityCountAtBlock()
    {
        super("Get Entity Count at Block",
            "Prints the list of entities within the occupied space of the block you are looking at.",
            KEY_BEHAVIOR.TRIGGER,
            config -> config.feature.getEntitiesInBlock,
            value -> ConfigManager.config.feature.getEntitiesInBlock = value);

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
