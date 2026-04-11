package spctreutils.feature.impl;

import spctreutils.SpctreUtils;
import spctreutils.config.ConfigManager;
import spctreutils.feature.Feature;

public class NoClip extends Feature
{
    public NoClip()
    {
        super("NoClip",
            "Allows you to clip through blocks. Must be in singleplayer.",
            config -> config.feature.noClip,
            value -> ConfigManager.config.feature.noClip = value);
    }

    @Override
    public void onDisabled()
    {
        if (SpctreUtils.localPlayer == null) return;
        SpctreUtils.localPlayer.getAbilities().flying = false;
    }

    @Override
    public void onTick()
    {
        if (mc.player == null || mc.getSingleplayerServer() == null) return;
        mc.player.fallDistance = 0;
        mc.player.getAbilities().flying = true;
    }
}
