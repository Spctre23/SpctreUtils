package spctreutils.module.feature.impl;

import spctreutils.module.feature.TriggerFeature;

public class SwitchSurvival extends TriggerFeature
{
    public SwitchSurvival()
    {
        super("Gamemode Survival Keybind", "Keybind to switch to survival.");
    }

    @Override
    protected void onKeyPressed()
    {
        mc.player.connection.sendCommand("gamemode survival");
    }
}
