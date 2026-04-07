package spctreutils.hud.impl;

import net.minecraft.world.item.ItemStack;
import spctreutils.config.ConfigManager;
import spctreutils.hud.HudElement;

import java.awt.*;

public class Durability extends HudElement
{
    public Durability()
    {
        super("Durability", Color.WHITE, () -> ConfigManager.config.durability);
    }

    @Override
    public void onTick()
    {
        ItemStack held = mc.player.getMainHandItem();
        if (held.isEmpty() || !held.isDamageableItem())
        {
            text = "";
            return;
        }
        int durability = held.getMaxDamage() - held.getDamageValue();
        text = "Durability: " + durability + " / " + held.getMaxDamage();
    }
}
