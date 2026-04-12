package spctreutils.hud.impl;

import net.minecraft.world.entity.animal.goat.Goat;
import spctreutils.hud.HudElement;

import java.awt.*;

public class GoatVariant extends HudElement
{
    public GoatVariant()
    {
        super("Goat Variant", config -> config.hud.goatVariant);
    }

    @Override
    public void onTick()
    {
        if (mc.crosshairPickEntity instanceof Goat goat)
        {
            boolean isScreaming = goat.isScreamingGoat();
            String variant = isScreaming ? "Screaming" : "Regular";
            Color color = isScreaming ? Color.RED : Color.lightGray;
            setText(variant, color);
            return;
        }
        removeContent();
    }
}
