package spctreutils.module.hud.impl;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.Horse;
import spctreutils.module.hud.HudElement;

public class HorseSpeed extends HudElement
{
    private final double speedConversion = 43.17; // Conversion to m/s

    public HorseSpeed()
    {
        super("Horse Speed", "Displays the max speed of the horse you are looking at.");
    }

    @Override
    protected void onTick()
    {
        if (mc.crosshairPickEntity instanceof Horse horse)
        {
            String speed = String.format("%.3f m/s", horse.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedConversion);
            setText(speed);
            return;
        }
        clearParts();
    }
}
