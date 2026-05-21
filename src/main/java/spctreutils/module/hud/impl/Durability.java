package spctreutils.module.hud.impl;

import net.minecraft.world.item.ItemStack;
import spctreutils.helper.ItemHelper;
import spctreutils.module.hud.HudElement;

public class Durability extends HudElement
{
    public Durability()
    {
        super("Durability", "Displays the durability of the item you are currently holding.");
    }

    @Override
    protected void onTick()
    {
        ItemStack held = mc.player.getMainHandItem();
        if (held.isEmpty() || !held.isDamageableItem())
        {
            clearParts();
            return;
        }

        String text = ItemHelper.getDurability(held) + " / " + ItemHelper.getMaxDurability(held);
        setText(text);
    }
}
