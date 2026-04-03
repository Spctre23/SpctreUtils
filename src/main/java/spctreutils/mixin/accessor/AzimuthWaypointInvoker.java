package spctreutils.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.world.waypoints.TrackedWaypoint$AzimuthWaypoint")
public interface AzimuthWaypointInvoker
{
    @Accessor("angle")
    float invokeAngle();
}
