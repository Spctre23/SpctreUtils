package spctreutils.module.hud.impl;

import spctreutils.module.hud.HudElement;

public class FPS extends HudElement
{
    public FPS()
    {
        super("FPS", "Displays your frames per second.");
    }

    @Override
    protected void onTick()
    {
        setText(String.valueOf(mc.getFps()));
    }
}
