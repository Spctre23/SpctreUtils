package spctreutils.feature.impl;

import spctreutils.feature.Feature;

public class Invulnerable extends Feature
{
    public Invulnerable()
    {
        super("Invulnerable", "Makes you invincible. Must be in singleplayer.");
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
