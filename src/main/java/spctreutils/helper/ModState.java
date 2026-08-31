package spctreutils.helper;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

public class ModState
{
    public static boolean commandExists(String commandName)
    {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        if (listener == null) return false;

        CommandDispatcher<ClientSuggestionProvider> dispatcher = listener.getCommands();
        return dispatcher.getRoot().getChild(commandName) != null;
    }
}
