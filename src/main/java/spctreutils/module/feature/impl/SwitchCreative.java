package spctreutils.module.feature.impl;

import spctreutils.module.feature.TriggerFeature;

public class SwitchCreative extends TriggerFeature
{
    public SwitchCreative()
    {
        super("Gamemode Creative Keybind", "Keybind to switch to creative mode.");
    }

    @Override
    protected void onKeyPressed()
    {
        mc.player.connection.sendCommand("gamemode creative");
    }
}
