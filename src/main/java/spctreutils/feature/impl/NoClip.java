package spctreutils.feature.impl;
import spctreutils.feature.Feature;

public class NoClip extends Feature
{
    public NoClip()
    {
        super("NoClip", "Allows you to clip through blocks. Must be in singleplayer.");
    }

    @Override
    public void onTick()
    {
        if (mc.getSingleplayerServer() == null) return;
        mc.player.fallDistance = 0;
        mc.player.getAbilities().flying = true;
    }

    @Override
    protected void onDisabled()
    {
        mc.player.getAbilities().flying = false;
    }
}
