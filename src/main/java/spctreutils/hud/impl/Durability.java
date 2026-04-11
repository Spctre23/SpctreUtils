package spctreutils.hud.impl;

import net.minecraft.world.item.ItemStack;
import spctreutils.hud.HudElement;

import java.awt.*;

public class Durability extends HudElement
{
    public Durability()
    {
        super("Durability", Color.WHITE, config -> config.hud.durability);
    }

    @Override
    public void onTick()
    {
        ItemStack held = mc.player.getMainHandItem();
        if (held.isEmpty() || !held.isDamageableItem())
        {
            removeContent();
            return;
        }
        int maxDamage = held.getMaxDamage();
        int durability = maxDamage - held.getDamageValue();
        String text = durability + " / " + maxDamage;
        setContent(text, Color.lightGray);
    }
}
