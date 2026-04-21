package spctreutils.hud.impl;

import net.minecraft.world.phys.Vec3;
import spctreutils.hud.HudElement;
import spctreutils.setting.Setting;

import java.util.List;

public class Acceleration extends HudElement
{
    private static final Setting<Boolean> measure_gForce = new Setting<>("Also display g-force", true, Boolean.class);
    private static final Setting<Integer> decimalPrecision = new Setting<>("Decimal precision", 1, Integer.class);

    private final double EARTH_GRAVITY = 9.80665;

    public Acceleration()
    {
        super("Acceleration", "Displays your acceleration in meters per second squared.", List.of(measure_gForce, decimalPrecision));
    }

    @Override
    protected void onTick()
    {
        Vec3 vel = mc.player.getDeltaMovement().scale(20);
        double accel = Math.sqrt(vel.x) + Math.sqrt(vel.y) + Math.sqrt(vel.z);
        String accelText = String.format("%.%f m/s²", accel, decimalPrecision.getValue());
        String gForceText = "";

        if (measure_gForce.getValue())
        {
            double gForce = accel * (1 / EARTH_GRAVITY);
            gForceText = String.format("| %.%f g's", gForce, decimalPrecision.getValue());
        }

        setText(accelText + gForceText);
    }
}
