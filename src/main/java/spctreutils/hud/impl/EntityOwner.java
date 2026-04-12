package spctreutils.hud.impl;

import net.minecraft.world.entity.TamableAnimal;
import spctreutils.hud.HudElement;

public class EntityOwner extends HudElement
{
    public EntityOwner()
    {
        super("Owner", config -> config.hud.entityOwner);
    }

    @Override
    public void onTick()
    {
        if (mc.crosshairPickEntity instanceof TamableAnimal entity && entity.isTame())
        {
            setText(entity.getOwner().getName().getString());
            return;
        }
        removeContent();
    }
}
