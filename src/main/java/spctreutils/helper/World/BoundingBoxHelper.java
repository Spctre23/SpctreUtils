package spctreutils.helper.World;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class BoundingBoxHelper
{
    public static BoundingBox toRegion(AABB box)
    {
        return new BoundingBox(
            (int) Math.floor(box.minX),
            (int) Math.floor(box.minY),
            (int) Math.floor(box.minZ),
            (int) Math.ceil(box.maxX) - 1,
            (int) Math.ceil(box.maxY) - 1,
            (int) Math.ceil(box.maxZ) - 1
        );
    }
}
