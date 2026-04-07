package spctreutils.hud.impl;

import net.minecraft.world.entity.TamableAnimal;
import spctreutils.config.ConfigManager;
import spctreutils.hud.HudElement;

import java.awt.*;

public class EntityOwner extends HudElement
{
    public EntityOwner()
    {
        super("Entity Owner", Color.WHITE, () -> ConfigManager.config.entityOwner);
    }

    @Override
    public void onTick()
    {
        if (mc.crosshairPickEntity instanceof TamableAnimal entity && entity.isTame())
        {
            text = entity.getOwner().getName().getString();
            return;
        }
        text = "";
    }
}
