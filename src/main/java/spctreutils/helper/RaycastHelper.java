package spctreutils.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import org.jetbrains.annotations.Nullable;

public class RaycastHelper
{
    private record Ray(Vec3 start, Vec3 look, Vec3 end) {}

    private static Ray getRay()
    {
        Minecraft mc = Minecraft.getInstance();
        Vec3 start = mc.gameRenderer.getMainCamera().getPosition();
        Vec3 look = mc.gameRenderer.getMainCamera().getEntity().getLookAngle();
        Vec3 end = start.add(look.scale(100));
        return new Ray(start, look, end);
    }

    @Nullable
    public static Entity getAimedEntity()
    {
        Minecraft mc = Minecraft.getInstance();
        Ray ray = getRay();
        Vec3 start = ray.start(), look = ray.look, end = ray.end();
        AABB searchBox = mc.player.getBoundingBox().expandTowards(look.scale(100)).inflate(1.0);

        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
            mc.level, mc.player, start, end, searchBox,
            entity -> !entity.isSpectator() && entity != mc.player,
            0.0f
        );
        return hit == null || hit.getType() == HitResult.Type.MISS || hit.getEntity() == null ? null : hit.getEntity();
    }

    @Nullable
    public static BlockPos getAimedBlock(boolean adjacent)
    {
        Minecraft mc = Minecraft.getInstance();
        Ray ray = getRay();
        Vec3 start = ray.start(), end = ray.end();

        BlockHitResult hit = mc.level.clip(new ClipContext(
            start, end,
            ClipContext.Block.OUTLINE,
            ClipContext.Fluid.NONE,
            mc.gameRenderer.getMainCamera().getEntity()
        ));

        if (hit.getType() == HitResult.Type.MISS) return null;

        BlockPos pos = hit.getBlockPos();
        if (!adjacent) return pos;
        return pos.relative(hit.getDirection());
    }

    @Nullable
    public static BlockPos getAimedBlock()
    {
        return getAimedBlock(false);
    }
}
