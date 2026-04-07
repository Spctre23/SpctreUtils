package spctreutils.hud.impl;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.TamableAnimal;
import spctreutils.config.ConfigManager;
import spctreutils.hud.HudElement;

import java.awt.*;

public class EntityOwner extends HudElement
{
    public EntityOwner()
    {
        super("Owner", Color.WHITE, () -> ConfigManager.config.entityOwner);
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
