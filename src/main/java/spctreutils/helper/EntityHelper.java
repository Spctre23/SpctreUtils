package spctreutils.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.function.Consumer;

public class EntityHelper
{

    public static <T extends Entity> HashSet<T> getEntitiesOfType(Class<T> entityType)
    {
        HashSet<T> entities = new HashSet<>();
        forEachOfType(entityType, entities::add);
        return entities;
    }

    public static HashSet<Entity> getEntities()
    {
        HashSet<Entity> entities = new HashSet<>();
        forEach(entities::add);
        return entities;
    }

    public static HashSet<Entity> getEntities(AABB aabb)
    {
        HashSet<Entity> entities = new HashSet<>();
        forEach(entity ->
        {
            if (aabb.intersects(entity.getBoundingBox()))
                entities.add(entity);
        });
        return entities;
    }

    public static <T extends Entity> void forEachOfType(Class<T> entityClass, Consumer<T> action)
    {
        Minecraft mc = Minecraft.getInstance();
        for (Entity entity : mc.level.entitiesForRendering())
        {
            if (entity != null && entityClass.isInstance(entity))
                action.accept((T) entity);
        }
    }

    public static void forEach(Consumer<Entity> action)
    {
        Minecraft mc = Minecraft.getInstance();
        for (Entity entity : mc.level.entitiesForRendering())
        {
            if (entity == null) continue;
            action.accept(entity);
        }
    }

    public static class Physics
    {
        private static final double EARTH_GRAVITY = 9.80665;

        private Vec3 lastPos = null;
        private Velocity lastVelocity = null;

        public record Velocity(double horizontal, double vertical, double total) {}

        public record Acceleration(double mpsSquared, double gForce) {}

        public Velocity getVelocity(Entity entity)
        {
            Vec3 currentPos = entity.position();
            Velocity velocity = null;

            if (lastPos != null)
            {
                double dx = currentPos.x - lastPos.x;
                double dy = currentPos.y - lastPos.y;
                double dz = currentPos.z - lastPos.z;
                velocity = new Velocity(
                    Math.sqrt(dx * dx + dz * dz) * 20,
                    dy * 20,
                    Math.sqrt(dx * dx + dy * dy + dz * dz) * 20
                );
            }

            lastPos = currentPos;
            return velocity;
        }

        public Acceleration getAcceleration(Velocity current)
        {
            Acceleration accel = null;

            if (lastVelocity != null && current != null)
            {
                double dv = Math.abs(current.total() - lastVelocity.total()) * 20;
                accel = new Acceleration(dv, dv / EARTH_GRAVITY);
            }

            lastVelocity = current;
            return accel;
        }

        public void reset()
        {
            lastPos = null;
            lastVelocity = null;
        }
    }
}
