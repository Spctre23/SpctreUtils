package spctreutils.hud.impl;

import net.minecraft.world.phys.Vec3;
import spctreutils.hud.HudElement;
import spctreutils.setting.Setting;

import java.util.List;

public class Speed extends HudElement
{
    private static final Setting<Boolean> verticalSpeed = new Setting<>("Also display vertical speed", false, Boolean.class);
    private static final Setting<Integer> decimalPrecision = new Setting<>("Decimal precision", 1, Integer.class);

    public Speed()
    {
        super("Speed", "Displays your speed in meters per second.", List.of(verticalSpeed, decimalPrecision));
    }

    @Override
    protected void onTick()
    {
        Vec3 vel = mc.player.getDeltaMovement().scale(20);
        double speed = vel.x + vel.y + vel.z;
        String speedText = String.format("%.%f m/s", speed, decimalPrecision.getValue());
        String vSpeedText = "";

        if (verticalSpeed.getValue())
        {
            double vSpeed = vel.y;
            vSpeedText = String.format(" | VS: %.%f m/s", vSpeed, decimalPrecision.getValue());
        }

        setText(speedText + vSpeedText);
    }
}
