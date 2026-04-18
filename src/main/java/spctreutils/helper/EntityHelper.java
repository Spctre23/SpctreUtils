package spctreutils.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

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
}
