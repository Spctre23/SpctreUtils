package spctreutils.hud.impl;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import spctreutils.helper.DimensionHelper;
import spctreutils.hud.HudElement;
import spctreutils.setting.Setting;

import java.util.List;

public class Position extends HudElement
{
    public static final Setting<Boolean> SCALED = new Setting<>(
        "Also display position scaled to opposite dimension",
        true,
        Boolean.class
    );

    public Position()
    {
        super("Position", "Pos", "Displays your coordinates.", List.of(SCALED));
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
        if (SCALED.getValue() == true)
        {
            Vec3 posScaled = DimensionHelper.getOppositePos(pos);
            posScaledText = String.format(" [ %.1f, %.1f, %.1f ]", posScaled.x, posScaled.y, posScaled.z);
        }

        setText(posText + posScaledText);
    }
}
