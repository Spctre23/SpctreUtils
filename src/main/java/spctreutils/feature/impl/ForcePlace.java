package spctreutils.feature.impl;

import spctreutils.config.ConfigManager;
import spctreutils.feature.Feature;

public class ForcePlace extends Feature
{
    public ForcePlace()
    {
        super("Force Place",
            config -> config.feature.forcePlace,
            value -> ConfigManager.config.feature.forcePlace = value);
    }
}
