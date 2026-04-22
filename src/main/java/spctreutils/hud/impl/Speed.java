package spctreutils.hud.impl;

import spctreutils.helper.EntityHelper;
import spctreutils.hud.HudElement;
import spctreutils.setting.Setting;

import java.util.List;

public class Speed extends HudElement
{
    private static final Setting<Boolean> verticalSpeed = new Setting<>("Also display vertical speed", false, Boolean.class);
    private static final Setting<Integer> decimalPrecision = new Setting<>("Decimal precision", 1, Integer.class);

    private final EntityHelper.Physics physics = new EntityHelper.Physics();

    public Speed()
    {
        super("Speed", "Displays your speed in meters per second.", List.of(verticalSpeed, decimalPrecision));
    }

    @Override
    protected void onTick()
    {
        EntityHelper.Physics.Velocity velocity = physics.getVelocity(mc.player);
        if (velocity == null) return;

        String speedText = String.format("§f%." + decimalPrecision.getValue() + "f m/s", velocity.total());
        String vSpeedText = "";
        if (verticalSpeed.getValue())
            vSpeedText = String.format(" §7y: §f%." + decimalPrecision.getValue() + "f m/s", velocity.vertical());

        setText(speedText + vSpeedText);
    }

    @Override
    protected void onEnabled() { physics.reset(); }

    @Override
    protected void onDisabled() { physics.reset(); }
}
