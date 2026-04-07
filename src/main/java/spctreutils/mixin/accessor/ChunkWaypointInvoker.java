package spctreutils.mixin.accessor;

import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.world.waypoints.TrackedWaypoint$ChunkWaypoint")
public interface ChunkWaypointInvoker
{
    @Invoker("position")
    Vec3 invokePosition(double d);
}
