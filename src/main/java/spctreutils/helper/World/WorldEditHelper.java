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
        selectArea(area);
        return sendCommand("/set %s".formatted(BlockStateParser.serialize(state)));
    }

    public static boolean undo()
    {
        return sendCommand("/undo");
    }

    public static boolean redo()
    {
        return sendCommand("/redo");
    }

    private static void selectArea(AABB area)
    {
        BoundingBox region = BoundingBoxHelper.toRegion(area);
        sendCommand("/pos1 %d,%d,%d".formatted(
            region.minX(),
            region.minY(),
            region.minZ()
        ));
        sendCommand("/pos2 %d,%d,%d".formatted(
            region.maxX(),
            region.maxY(),
            region.maxZ()
        ));
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
