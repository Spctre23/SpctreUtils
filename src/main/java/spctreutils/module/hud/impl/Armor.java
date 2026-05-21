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

        setLayout(new Layout(AttachTo.BOTTOM_RIGHT, false, 25, 15));
    }

    @Override
    protected void onTick()
    {
        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = mc.player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = mc.player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = mc.player.getItemBySlot(EquipmentSlot.FEET);

        setArmorPart(helmet, 0);
        setArmorPart(chestplate, 1);
        setArmorPart(leggings, 2);
        setArmorPart(boots, 3);
    }

    private void setArmorPart(ItemStack item, int x)
    {
        if (ItemHelper.isArmor(item))
        {
            setText(String.valueOf(ItemHelper.getDurability(item)), item.getBarColor(), x, -1);
        }
        else setText("", x, -1);

        setItem(item.getItem(), x, 0);
    }
}