package spctreutils.module.hud.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import spctreutils.helper.DimensionHelper;
import spctreutils.module.hud.HudElement;
import spctreutils.setting.Setting;

import java.util.List;

public class Position extends HudElement
{
    private static final Setting<Boolean> scaled = new Setting<>("Also display opposite dimension pos", false, Boolean.class);

    public Position()
    {
        super("Position", "Pos", "Displays your coordinates.", List.of(scaled));

        setLayout(new Layout(AttachTo.BOTTOM_LEFT, false, 15, 15));
    }

    @Override
    protected void onTick()
    {
        BlockPos pos = mc.player.blockPosition();
        String posText = String.format("§7x: §f%d §7y: §f%d §7z: §f%d", pos.getX(), pos.getY(), pos.getZ());
        String posScaledText = "";

        if (scaled.getValue())
        {
            BlockPos posScaled = DimensionHelper.getOppositePos(pos);
            posScaledText = String.format(" [§7x: §f%d §7y: §f%d §7z: §f%d]", posScaled.getX(), pos.getY(), posScaled.getZ());
        }

        setText(posText + posScaledText);
    }
}
