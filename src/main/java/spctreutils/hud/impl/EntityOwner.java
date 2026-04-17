package spctreutils.hud.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import spctreutils.hud.HudElement;

public class EntityOwner extends HudElement
{
    public EntityOwner()
    {
        super("Entity Owner", "Owner", "Displays the owner of the entity you are looking at, if tamed.");
    }

    @Override
    protected void onTick()
    {
        if (mc.crosshairPickEntity instanceof TamableAnimal entity && entity.isTame())
        {
            Entity owner = entity.getOwner();
            if (entity.getOwner() != null)
                setText(owner.getName().getString());
        }
        removeText();
    }
}
