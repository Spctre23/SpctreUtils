package spctreutils.hud.impl;

import net.minecraft.client.multiplayer.PlayerInfo;
import spctreutils.helper.TickHelper;
import spctreutils.hud.HudElement;

public class Ping extends HudElement
{
    public Ping()
    {
        super("Ping", "Displays your ping.");
    }

    @Override
    protected void onTick()
    {
        PlayerInfo info = mc.player.connection.getPlayerInfo(mc.player.getUUID());
        String text = info == null ? String.valueOf(0) : String.valueOf(info.getLatency());
        setText(text);
    }
}
