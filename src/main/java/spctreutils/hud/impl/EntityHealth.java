package spctreutils.hud.impl;

import net.minecraft.world.entity.LivingEntity;
import spctreutils.config.ConfigManager;
import spctreutils.hud.HudElement;

public class EntityHealth extends HudElement
{
    public EntityHealth()
    {
        super("Entity Health", "HP", "Displays the health of the entity you are looking at.");
    }

    @Override
    protected void onTick()
    {
        if (mc.crosshairPickEntity instanceof LivingEntity entity)
        {
            String healthText = String.format("%.1f / %.1f", entity.getHealth(), entity.getMaxHealth());
            setText(healthText);
            return;
        }
        removeText();
    }
}
