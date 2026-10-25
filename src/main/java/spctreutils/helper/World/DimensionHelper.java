package spctreutils.helper.World;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class DimensionHelper
{
    private static final Minecraft mc = Minecraft.getInstance();

    public static Vec3 getOppositePos(Vec3 pos)
    {
        ResourceKey<Level> dimension = mc.level.dimension();
        if (dimension != Level.END)
            pos = dimension == Level.NETHER ? pos.scale(8) : pos.scale(0.125);
        return pos;
    }

    public static BlockPos getOppositePos(BlockPos pos)
    {
        Vec3 vec3 = getOppositePos(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
        return new BlockPos((int)vec3.x, (int)vec3.y, (int)vec3.z);
    }

    public static String getDimensionPath()
    {
        if (mc.level == null) return "None";
        return mc.level.dimension().identifier().getPath();
    }
}
