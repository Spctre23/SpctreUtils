package spctreutils.module.feature.impl;

import spctreutils.module.feature.Feature;

public class SwitchSpectator extends Feature
{
    public SwitchSpectator()
    {
        super("Gamemode Spectator Keybind", "Keybind to switch to spectator mode.", KEY_BEHAVIOR.TRIGGER);
    }

    @Override
    protected void onKeyPressed()
    {
        mc.player.connection.sendCommand("gamemode spectator");
    }
}
