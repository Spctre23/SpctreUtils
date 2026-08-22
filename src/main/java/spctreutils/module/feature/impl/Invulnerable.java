package spctreutils.module.feature.impl;

import net.minecraft.world.entity.player.Player;
import spctreutils.SpctreUtils;
import spctreutils.module.feature.ToggleFeature;

public class Invulnerable extends ToggleFeature
{
    public Invulnerable()
    {
        super("Invulnerable", "Makes you invincible. Must be in singleplayer.");
    }

    @Override
    protected void onTick()
    {
        Player player = SpctreUtils.serverPlayer;
        if (player == null) return;
        player.setInvulnerable(true);
    }

    @Override
    protected void onDisabled()
    {
        Player player = SpctreUtils.serverPlayer;
        if (player == null ||
            player.isSpectator() ||
            player.isCreative()) return;
        player.setInvulnerable(false);
    }
}
