package spctreutils.helper.World;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public final class FillDispatcher
{
    private static final ArrayDeque<String> QUEUE = new ArrayDeque<>();
    private static final int COMMANDS_PER_TICK = 10;

    private static int cooldown = 0;

    public static void init()
    {
        ClientTickEvents.END_CLIENT_TICK.register(client ->
        {
            if (client.player == null || QUEUE.isEmpty()) return;
            if (cooldown-- > 0) return;
            cooldown = COMMANDS_PER_TICK;
            client.player.connection.sendCommand(QUEUE.poll());
        });
    }

    public static void queueFill(AABB area, Block blockType)
    {
        queueFill(area, blockType, 32767);
    }

    public static void queueFill(AABB area, Block blockType, int maxVolume)
    {
        BoundingBox region = BoundingBoxHelper.toRegion(area);
        for (BoundingBox slice : sliceByY(region, maxVolume))
        {
            QUEUE.add("fill %d %d %d %d %d %d %s".formatted(
                slice.minX(), slice.minY(), slice.minZ(),
                slice.maxX(), slice.maxY(), slice.maxZ(), BuiltInRegistries.BLOCK.getKey(blockType)));
        }
    }

    private static List<BoundingBox> sliceByY(BoundingBox r, int maxVolume)
    {
        List<BoundingBox> out = new ArrayList<>();
        long layerSize = (long) r.getXSpan() * r.getZSpan();
        int layers = (int) Math.max(1, maxVolume / Math.max(1, layerSize));

        for (int y = r.minY(); y <= r.maxY(); y += layers)
        {
            int top = Math.min(y + layers - 1, r.maxY());
            out.add(new BoundingBox(r.minX(), y, r.minZ(), r.maxX(), top, r.maxZ()));
        }
        return out;
    }
}
