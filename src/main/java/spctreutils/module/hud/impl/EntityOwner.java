package spctreutils.module.hud.impl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import spctreutils.module.hud.HudElement;

public class EntityOwner extends HudElement
{
    public EntityOwner()
    {
        super("Entity Owner", "Owner", "Displays the owner of the entity you are looking at, if tamed.");
    }

    @Override
    protected void onTick()
    {
        if (mc.crosshairPickEntity instanceof OwnableEntity entity)
        {
            Entity owner = entity.getOwner();
            if (entity.getOwner() != null)
                setText(owner.getName().getString());
            return;
        }
        clearElements();
    }
}
