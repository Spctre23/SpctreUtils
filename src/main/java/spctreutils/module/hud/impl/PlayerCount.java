package spctreutils.module.hud.impl;

import net.minecraft.client.multiplayer.PlayerInfo;
import spctreutils.module.hud.HudElement;

public class PlayerCount extends HudElement
{
    public PlayerCount()
    {
        super("Player Count", "Players", "Displays player count on the server.");
    }

    @Override
    protected void onTick()
    {
        int count = mc.player.connection.getOnlinePlayers().size();
        setText(String.valueOf(count));
    }
}
