package spctreutils.hud.impl;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import spctreutils.config.ConfigManager;
import spctreutils.hud.HudElement;

public class Position extends HudElement
{
    public Position()
    {
        super("Position",
            "Pos",
            "Displays your coordinates.",
            config -> config.position,
            value -> ConfigManager.config.position = value);
    }

    @Override
    public void onTick()
    {
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null)
        {
            removeText();
            return;
        }

        Vec3 pos = player.position();
        String posText = String.format("%.0f, %.0f, %.0f", pos.x, pos.y, pos.z);
        String posScaledText = "";
        if (ConfigManager.config.positionScaled)
        {
            ResourceKey dimension = mc.level.dimension();
            if (dimension != Level.END)
            {
                Vec3 posScaled = dimension == Level.NETHER ? pos.scale(8) : pos.scale(0.125);
                posScaledText = String.format(" [ %.1f, %.1f, %.1f ]", posScaled.x, posScaled.y, posScaled.z);
            }
        }

        setText(posText + posScaledText);
    }
}
