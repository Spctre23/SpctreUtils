package spctreutils.mixin.accessor;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.PartialTickSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "net.minecraft.world.waypoints.TrackedWaypoint$Vec3iWaypoint")
public interface Vec3iWaypointInvoker
{
    @Invoker("position")
    Vec3 invokePosition(Level level, PartialTickSupplier partialTickSupplier);
}

