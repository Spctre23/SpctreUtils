package spctreutils.hud.impl;

import net.minecraft.world.entity.animal.goat.Goat;
import spctreutils.hud.HudElement;

import java.awt.*;

public class GoatVariant extends HudElement
{
    public GoatVariant()
    {
        super("Goat Variant", "Displays the variant of the goat you are looking at.");
    }

    @Override
    protected void onTick()
    {
        if (mc.crosshairPickEntity instanceof Goat goat)
        {
            boolean isScreaming = goat.isScreamingGoat();
            String variant = isScreaming ? "Screaming" : "Regular";
            int variantColor = isScreaming ? Color.RED.getRGB() : textColor;
            setText(variant, variantColor);
            return;
        }
        removeText();
    }
}
