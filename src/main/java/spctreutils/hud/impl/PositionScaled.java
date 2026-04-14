package spctreutils.hud.impl;

import spctreutils.config.ConfigManager;
import spctreutils.hud.HudElement;

public class PositionScaled extends HudElement
{
    public PositionScaled()
    {
        super("Position (scaled)",
            "Displays your current coordinates, scaled to the opposite dimension.",
            config -> config.positionScaled,
            value -> ConfigManager.config.positionScaled = value);
    }
}
