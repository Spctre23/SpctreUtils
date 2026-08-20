package spctreutils.helper.World;

import net.minecraft.client.Camera;
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
        Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
        Vec3 start = camera.position();
        Vec3 look = camera.entity().getLookAngle();
        Vec3 end = start.add(look.scale(100));
        return new Ray(start, look, end);
    }

    public static BlockHitResult getBlockHitResult()
    {
        Minecraft mc = Minecraft.getInstance();
        Ray ray = getRay();

        return mc.level.clip(new ClipContext(
            ray.start, ray.end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, mc.gameRenderer.mainCamera().entity()
        ));
    }

    public static BlockPos getAimedBlock(boolean adjacent)
    {
        BlockHitResult hit = getBlockHitResult();
        if (hit.getType() == HitResult.Type.MISS) return null;

        BlockPos pos = hit.getBlockPos();
        if (!adjacent) return pos;
        return pos.relative(hit.getDirection());
    }

    public static BlockPos getAimedBlock()
    {
        return getAimedBlock(false);
    }

    @Nullable
    public static Entity getAimedEntity()
    {
        Minecraft mc = Minecraft.getInstance();
        Ray ray = getRay();
        AABB searchBox = mc.player.getBoundingBox().expandTowards(ray.look.scale(100)).inflate(1.0);

        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
            mc.level, mc.player, ray.start, ray.end, searchBox, entity -> !entity.isSpectator() && entity != mc.player, 0.0f
        );
        return hit == null || hit.getType() == HitResult.Type.MISS ? null : hit.getEntity();
    }
}
