package spctreutils.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.HashSet;
import java.util.function.Consumer;

public class ChunkHelper
{
    public static void forEach(Consumer<LevelChunk> action)
    {
        Minecraft mc = Minecraft.getInstance();
        int renderDist = mc.options.renderDistance().get();
        int chunkX = mc.player.chunkPosition().x;
        int chunkZ = mc.player.chunkPosition().z;

        for (int x = chunkX - renderDist; x <= chunkX + renderDist; x++)
        {
            for (int z = chunkZ - renderDist; z <= chunkZ + renderDist; z++)
            {
                LevelChunk chunk = mc.level.getChunk(x, z);
                if (chunk == null) continue;
                action.accept(chunk);
            }
        }
    }

    public static <T extends BlockEntity> HashSet<T> getBlockEntitiesOfType(Class<T> blockEntityType)
    {
        HashSet<T> blockEntities = new HashSet<>();
        forEach(chunk ->
            chunk.getBlockEntities().values().stream()
                .filter(blockEntityType::isInstance)
                .map(blockEntityType::cast)
                .forEach(blockEntities::add));

        return blockEntities;
    }
}
