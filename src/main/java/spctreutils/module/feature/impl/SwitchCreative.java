package spctreutils.module.feature.impl;

import spctreutils.module.feature.Feature;

public class SwitchCreative extends Feature
{
    public SwitchCreative()
    {
        super("Gamemode Creative Keybind", "Keybind to switch to creative mode.", KEY_BEHAVIOR.TRIGGER);
    }

    @Override
    protected void onKeyPressed()
    {
        mc.player.connection.sendCommand("gamemode creative");
    }
}
