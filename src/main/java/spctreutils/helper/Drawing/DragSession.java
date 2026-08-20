package spctreutils.helper.Drawing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class DragSession
{
    private final BlockPos anchor;
    private final Vec3 anchorCenter;
    private final DragPlane plane;

    private int depth = 0;

    private DragSession(BlockPos anchor, DragPlane plane)
    {
        this.anchor = anchor;
        this.anchorCenter = Vec3.atCenterOf(anchor);
        this.plane = plane;
    }

    public static DragSession begin(BlockPos anchor, Direction hitFace)
    {
        return anchor == null ? null : new DragSession(anchor, DragPlane.lockToFace(hitFace));
    }

    public void adjustDepth(int delta)
    {
        depth = Math.clamp(depth + delta, -DragShape.MAX_DEPTH, DragShape.MAX_DEPTH);
    }

    public AABB currentBox(Vec3 camPos, Vec3 look, DragShape shape)
    {
        Vec3 hit = plane.intersect(camPos, look, anchorCenter);
        if (hit == null) return null;

        Vec3i offset = shape.collapse(hit.subtract(anchorCenter), plane.facing(), depth);
        return AABB.encapsulatingFullBlocks(anchor, anchor.offset(offset));
    }
}