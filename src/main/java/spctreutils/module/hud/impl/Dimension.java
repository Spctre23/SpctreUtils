package spctreutils.module.hud.impl;

import spctreutils.helper.DimensionHelper;
import spctreutils.module.hud.HudElement;

public class Dimension extends HudElement
{
    public Dimension()
    {
        super("Dimension", "Displays the dimension you are in.");
    }

    @Override
    protected void onTick()
    {
        setText(DimensionHelper.getDimension());
    }
}
