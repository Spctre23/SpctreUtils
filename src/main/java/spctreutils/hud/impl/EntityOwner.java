package spctreutils.hud.impl;

import net.minecraft.world.entity.TamableAnimal;
import spctreutils.hud.HudElement;

import java.awt.*;

public class EntityOwner extends HudElement
{
    public EntityOwner()
    {
        super("Owner", Color.WHITE, config -> config.entityOwner);
    }

    @Override
    public void onTick()
    {
        if (mc.crosshairPickEntity instanceof TamableAnimal entity && entity.isTame())
        {
            setContent(entity.getOwner().getName().getString(), Color.lightGray);
            return;
        }
        removeContent();
    }
}
