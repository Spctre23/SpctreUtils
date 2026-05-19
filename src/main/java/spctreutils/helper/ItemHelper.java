package spctreutils.helper;

import net.minecraft.world.item.ItemStack;

public class ItemHelper
{
    public static int getDurability(ItemStack item)
    {
        return getMaxDurability(item) - item.getDamageValue();
    }

    public static int getMaxDurability(ItemStack item)
    {
        return item.getMaxDamage();
    }
}
