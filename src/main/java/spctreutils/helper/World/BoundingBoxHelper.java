package spctreutils.helper.World;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

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

    private static List<BoundingBox> slice(BoundingBox r)
    {
        List<BoundingBox> out = new ArrayList<>();
        long layerSize = (long) r.getXSpan() * r.getZSpan();
        int layers = (int) Math.max(1, 32767 / Math.max(1, layerSize));

        for (int y = r.minY(); y <= r.maxY(); y += layers)
        {
            int top = Math.min(y + layers - 1, r.maxY());
            out.add(new BoundingBox(r.minX(), y, r.minZ(), r.maxX(), top, r.maxZ()));
        }
        return out;
    }
}
