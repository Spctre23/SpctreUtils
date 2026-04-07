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
    protected void onEnabled()
    {
        mc.player.setInvulnerable(true);
    }

    @Override
    public void onDisabled()
    {
        if (mc.player.isSpectator() || mc.player.isCreative()) return;
        mc.player.setInvulnerable(false);
    }
}
