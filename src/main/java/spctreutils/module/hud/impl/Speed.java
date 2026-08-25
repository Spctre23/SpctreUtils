package spctreutils.module.hud.impl;

import spctreutils.helper.World.EntityHelper;
import spctreutils.module.hud.HudElement;
import spctreutils.setting.Setting;

import java.util.List;

public class Speed extends HudElement
{
    private static final Setting<Boolean> VERTICAL_SPEED = new Setting<>("Also display vertical speed", false, Boolean.class);
    private static final Setting<Integer> DECIMAL_PRECISION = new Setting<>("Decimal precision", 1, Integer.class);

    private final EntityHelper.Physics physics = new EntityHelper.Physics();

    public Speed()
    {
        super("Speed", "Displays your speed in meters per second.", List.of(VERTICAL_SPEED, DECIMAL_PRECISION));
    }

    @Override
    protected void onTick()
    {
        EntityHelper.Physics.Velocity velocity = physics.getVelocity(mc.player);
        if (velocity == null) return;

        String speedText = String.format("§f%." + DECIMAL_PRECISION.getValue() + "f m/s", velocity.total());
        String vSpeedText = "";
        if (VERTICAL_SPEED.getValue())
            vSpeedText = String.format(" §7y: §f%." + DECIMAL_PRECISION.getValue() + "f m/s", velocity.vertical());

        setText(speedText + vSpeedText);
    }

    @Override
    protected void onEnabled() { physics.reset(); }

    @Override
    protected void onDisabled() { physics.reset(); }
}