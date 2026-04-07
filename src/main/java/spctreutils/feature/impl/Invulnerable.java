package spctreutils.feature.impl;

import spctreutils.config.ConfigManager;
import spctreutils.feature.Feature;

public class Invulnerable extends Feature
{
    public Invulnerable()
    {
        super("Invulnerable", "Makes you invincible.", () -> ConfigManager.config.invulnerable, value -> ConfigManager.config.invulnerable = value);
    }

    @Override
    public void onEnable()
    {
        mc.player.setInvulnerable(enabled);
    }

    @Override
    public void onDisable()
    {
        if (mc.player.isSpectator() || mc.player.isCreative()) return;
        mc.player.setInvulnerable(enabled);
    }
}
