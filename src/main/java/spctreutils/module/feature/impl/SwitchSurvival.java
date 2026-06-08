package spctreutils.module.feature.impl;

import spctreutils.module.feature.Feature;

public class SwitchSurvival extends Feature
{
    public SwitchSurvival()
    {
        super("Gamemode Survival Keybind", "Keybind to switch to survival.", KEY_BEHAVIOR.TRIGGER);
    }

    @Override
    protected void onKeyPressed()
    {
        mc.player.connection.sendCommand("gamemode survival");
    }
}
