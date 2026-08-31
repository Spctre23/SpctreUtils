package spctreutils.helper.World;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import spctreutils.helper.ModState;

public class WorldEditHelper
{
    public static boolean fillArea(AABB area, BlockState state)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !ModState.commandExists("worldedit")) return false;

        ClientPacketListener listener = player.connection;
        BoundingBox region = BoundingBoxHelper.toRegion(area);
        listener.sendCommand("/pos1 %d,%d,%d".formatted(
            region.minX(),
            region.minY(),
            region.minZ()
        ));
        listener.sendCommand("/pos2 %d,%d,%d".formatted(
            region.maxX(),
            region.maxY(),
            region.maxZ()
        ));
        listener.sendCommand("/set %s".formatted(BlockStateParser.serialize(state)));

        return true;
    }

    public static boolean undo()
    {
        return sendCommand("/undo");
    }

    public static boolean redo()
    {
        return sendCommand("/redo");
    }

    private static boolean sendCommand(String command)
    {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !ModState.commandExists("worldedit")) return false;

        ClientPacketListener listener = player.connection;
        listener.sendCommand(command);
        return true;
    }
}
