package spctreutils.module.hud.impl;

import spctreutils.helper.World.EntityHelper;
import spctreutils.module.hud.HudElement;
import spctreutils.setting.Setting;

import java.util.List;

public class Acceleration extends HudElement
{
    private static final Setting<Boolean> MEASURE_G_FORCE = new Setting<>("Also display g-force", false, Boolean.class);
    private static final Setting<Integer> DECIMAL_PRECISION = new Setting<>("Decimal precision", 1, Integer.class);

    private final EntityHelper.Physics physics = new EntityHelper.Physics();

    public Acceleration()
    {
        super("Acceleration", "Displays your acceleration in meters per second squared.", List.of(MEASURE_G_FORCE, DECIMAL_PRECISION));
    }

    @Override
    protected void onTick()
    {
        EntityHelper.Physics.Acceleration accel = physics.getAcceleration(physics.getVelocity(mc.player));
        if (accel == null) return;

        String accelText = String.format("§f%." + DECIMAL_PRECISION.getValue() + "f §7m/s²", accel.mpsSquared());
        String gForceText = "";
        if (MEASURE_G_FORCE.getValue())
            gForceText = String.format(" §f%." + DECIMAL_PRECISION.getValue() + "f §7g's", accel.gForce());

        setText(accelText + gForceText);
    }

    @Override
    protected void onEnabled() { physics.reset(); }

    @Override
    protected void onDisabled() { physics.reset(); }
}
