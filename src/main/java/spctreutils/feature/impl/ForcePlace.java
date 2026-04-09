package spctreutils.feature.impl;

import spctreutils.config.ConfigManager;
import spctreutils.feature.Feature;

public class ForcePlace extends Feature
{
    public ForcePlace()
    {
        super("Force Place",
            "Bypass placement restrictions. Must be in singleplayer.",
            false,
            config -> config.forcePlace,
            value -> ConfigManager.config.forcePlace = value);
    }
}
