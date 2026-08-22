package spctreutils.module.feature.impl;

import spctreutils.module.feature.TriggerFeature;

public class SwitchSpectator extends TriggerFeature
{
    public SwitchSpectator()
    {
        super("Gamemode Spectator Keybind", "Keybind to switch to spectator mode.");
    }

    @Override
    protected void onKeyPressed()
    {
        mc.player.connection.sendCommand("gamemode spectator");
    }
}
