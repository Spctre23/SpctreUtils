package spctreutils.module.hud.impl;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import spctreutils.helper.ItemHelper;
import spctreutils.module.hud.HudElement;

public class Armor extends HudElement
{
    public Armor()
    {
        super("Armor", "", "Displays your armor and durability.");

        setElementFlags(new ElementFlags(AttachType.BOTTOM_LEFT, false));
        setSubElementFlags(new SubElementFlags(25, 15));
    }

    @Override
    protected void onTick()
    {
        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        if (!helmet.isEmpty() && helmet.isDamageableItem())
        {
            setText(String.valueOf(ItemHelper.getDurability(helmet)), helmet.getBarColor(), 0, -1);
            setItem(helmet.getItem(), 0, 0);
        }

        ItemStack chestplate = mc.player.getItemBySlot(EquipmentSlot.CHEST);
        if (!chestplate.isEmpty() && chestplate.isDamageableItem())
        {
            setText(String.valueOf(ItemHelper.getDurability(chestplate)), chestplate.getBarColor(), 1, -1);
            setItem(chestplate.getItem(), 1, 0);
        }

        ItemStack leggings = mc.player.getItemBySlot(EquipmentSlot.LEGS);
        if (!leggings.isEmpty() && leggings.isDamageableItem())
        {
            setText(String.valueOf(ItemHelper.getDurability(leggings)), leggings.getBarColor(), 2, -1);
            setItem(leggings.getItem(), 2, 0);
        }

        ItemStack boots = mc.player.getItemBySlot(EquipmentSlot.FEET);
        if (!boots.isEmpty() && boots.isDamageableItem())
        {
            setText(String.valueOf(ItemHelper.getDurability(boots)), boots.getBarColor(), 3, -1);
            setItem(boots.getItem(), 3, 0);
        }
    }
}