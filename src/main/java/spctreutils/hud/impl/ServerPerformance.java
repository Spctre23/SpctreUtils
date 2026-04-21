package spctreutils.hud.impl;

import spctreutils.helper.TickHelper;
import spctreutils.hud.HudElement;

public class ServerPerformance extends HudElement
{
    public ServerPerformance()
    {
        super("Server Performance", "", "Displays server TPS and MSPT.");
    }

    @Override
    protected void onTick()
    {
        setText(String.format("TPS: %.2f / 20.0 | MSPT: %.2f / ", TickHelper.getTPS(), TickHelper.getTPS()));
    }
}
