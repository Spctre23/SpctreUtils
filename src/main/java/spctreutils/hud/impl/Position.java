package spctreutils.hud.impl;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import spctreutils.helper.DimensionHelper;
import spctreutils.hud.HudElement;
import spctreutils.setting.Setting;

import java.util.List;

public class Position extends HudElement
{
    private static final Setting<Boolean> scaled = new Setting<>("Also display opposite dimension pos", false, Boolean.class);

    public Position()
    {
        super("Position", "Pos", "Displays your coordinates.", List.of(scaled));
    }

    @Override
    protected void onTick()
    {
        Vec3 pos = mc.player.position();
        String posText = String.format("%.0f, %.0f, %.0f", pos.x, pos.y, pos.z);
        String posScaledText = "";

        if (scaled.getValue())
        {
            Vec3 posScaled = DimensionHelper.getOppositePos(pos);
            posScaledText = String.format(" [%.1f, %.1f, %.1f]", posScaled.x, posScaled.y, posScaled.z);
        }

        setText(posText + posScaledText);
    }
}
