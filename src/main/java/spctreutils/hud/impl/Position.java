package spctreutils.hud.impl;

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
        String posText = String.format("§7x: §f%.0f §7y: §f%.0f §7z: §f%.0f", pos.x, pos.y, pos.z);
        String posScaledText = "";

        if (scaled.getValue())
        {
            Vec3 posScaled = DimensionHelper.getOppositePos(pos);
            posScaledText = String.format(" [§7x: §f%.1f §7y: §f%.1f §7z: §f%.1f]", posScaled.x, pos.y, posScaled.z);
        }

        setText(posText + posScaledText);
    }
}
