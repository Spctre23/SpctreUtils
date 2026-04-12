package spctreutils.hud.impl;

import net.minecraft.world.entity.LivingEntity;
import spctreutils.hud.HudElement;

public class EntityHealth extends HudElement
{
    public EntityHealth()
    {
        super("HP", config -> config.hud.entityHealth);
    }

    @Override
    public void onTick()
    {
        if (mc.crosshairPickEntity instanceof LivingEntity entity)
        {
            String healthText = String.format("%.1f / %.1f", entity.getHealth(), entity.getMaxHealth());
            setText(healthText);
            return;
        }
        removeContent();
    }
}
