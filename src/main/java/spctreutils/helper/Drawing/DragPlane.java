package spctreutils.helper.Drawing;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public record DragPlane(Direction facing, Vec3 normal)
{
    private static final double MIN_INCIDENCE = 0.2;

    private static final double MIN_DEPTH = 0.05;

    public static DragPlane lockToFace(Direction hitFace)
    {
        Direction d = hitFace.getOpposite();
        return new DragPlane(d, new Vec3(d.getStepX(), d.getStepY(), d.getStepZ()));
    }

    public Vec3 intersect(Vec3 camPos, Vec3 look, Vec3 anchorCenter)
    {
        double incidence = look.dot(normal);
        if (Math.abs(incidence) < MIN_INCIDENCE) return null;

        double t = anchorCenter.subtract(camPos).dot(normal) / incidence;
        if (t <= MIN_DEPTH) return null;

        return camPos.add(look.scale(t));
    }
}