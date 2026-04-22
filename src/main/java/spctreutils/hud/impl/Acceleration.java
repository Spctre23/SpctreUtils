package spctreutils.hud.impl;

import net.minecraft.world.phys.Vec3;
import spctreutils.helper.EntityHelper;
import spctreutils.hud.HudElement;
import spctreutils.setting.Setting;

import java.util.List;

public class Acceleration extends HudElement
{
    private static final Setting<Boolean> measure_gForce = new Setting<>("Also display g-force", true, Boolean.class);
    private static final Setting<Integer> decimalPrecision = new Setting<>("Decimal precision", 1, Integer.class);

    private final EntityHelper.Physics physics = new EntityHelper.Physics();

    public Acceleration()
    {
        super("Acceleration", "Displays your acceleration in meters per second squared.", List.of(measure_gForce, decimalPrecision));
    }

    @Override
    protected void onTick()
    {
        EntityHelper.Physics.Acceleration accel = physics.getAcceleration(physics.getVelocity(mc.player));
        if (accel == null) return;

        String accelText = String.format("§f%." + decimalPrecision.getValue() + "f §7m/s²", accel.mpsSquared());
        String gForceText = "";
        if (measure_gForce.getValue())
            gForceText = String.format(" §f%." + decimalPrecision.getValue() + "f §7g's", accel.gForce());

        setText(accelText + gForceText);
    }

    @Override
    protected void onEnabled() { physics.reset(); }

    @Override
    protected void onDisabled() { physics.reset(); }
}
