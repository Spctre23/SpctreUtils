package spctreutils.helper.Drawing;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public enum DragShape
{
    LINE, BOX;

    public static final int MAX_LENGTH = 64;
    public static final int MAX_DEPTH = 64;

    public Vec3i collapse(Vec3 delta, Direction facing, int depth)
    {
        return switch (this)
        {
            case LINE ->
            {
                Direction d = Direction.getApproximateNearest(delta.x, delta.y, delta.z);
                int len = Math.clamp(
                    (int) Math.round(Math.abs(d.getAxis().choose(delta.x, delta.y, delta.z))),
                    0, MAX_LENGTH);
                yield new Vec3i(d.getStepX() * len, d.getStepY() * len, d.getStepZ() * len);
            }

            case BOX ->
            {
                Direction d = facing.getOpposite();
                yield new Vec3i(
                    component(delta.x, d.getStepX(), depth),
                    component(delta.y, d.getStepY(), depth),
                    component(delta.z, d.getStepZ(), depth));
            }
        };
    }

    private static int component(double delta, int depthStep, int depth)
    {
        return depthStep != 0 ? depthStep * Math.clamp(depth, -MAX_DEPTH, MAX_DEPTH) : inPlane(delta);
    }

    private static int inPlane(double v)
    {
        return Math.clamp((int) Math.round(v), -MAX_LENGTH, MAX_LENGTH);
    }
}