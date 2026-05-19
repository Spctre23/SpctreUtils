package spctreutils.module.hud.impl;

import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import spctreutils.helper.EntityHelper;
import spctreutils.module.hud.HudElement;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.List;

public class Armor extends HudElement
{
    private final EntityHelper.Physics physics = new EntityHelper.Physics();

    public Armor()
    {
        super("Armor", "", "Displays your armor and durability.");
    }

    @Override
    protected void onTick()
    {
        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet != null)
        {
            setText(String.valueOf(helmet.getDamageValue()), 0, 10);
            setItem(helmet.getItem(), 0, 0);
        }

        ItemStack chestplate = mc.player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate != null)
        {
            setText(1, String.valueOf(helmet.getDamageValue()), Color.WHITE, 10, 10);
            setItem(1, chestplate.getItem(), 10, 0);
        }

        ItemStack leggings = mc.player.getItemBySlot(EquipmentSlot.LEGS);
        if (leggings != null)
        {
            setText(2, String.valueOf(helmet.getDamageValue()), Color.WHITE, 20, 10);
            setItem(2, leggings.getItem(), 20, 0);
        }

        ItemStack boots = mc.player.getItemBySlot(EquipmentSlot.FEET);
        if (boots != null)
        {
            setText(3, String.valueOf(helmet.getDamageValue()), Color.WHITE, 30, 10);
            setItem(3, boots.getItem(), 30, 0);
        }
    }

    @Override
    protected void onEnabled() { physics.reset(); }

    @Override
    protected void onDisabled() { physics.reset(); }
}