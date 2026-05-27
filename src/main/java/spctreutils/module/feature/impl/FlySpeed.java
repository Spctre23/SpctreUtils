package spctreutils.module.feature.impl;

import dev.isxander.yacl3.gui.utils.KeyUtils;
import spctreutils.helper.Msg;
import spctreutils.module.feature.Feature;
import spctreutils.setting.Setting;

import java.util.List;

public class FlySpeed extends Feature
{
    public static final Setting<Float> speed = new Setting<>("Speed", 0.1f, Float.class);
    public static final Setting<Boolean> noDrag = new Setting<>("No Drag", false, Boolean.class);

    public FlySpeed()
    {
        super("Fly Speed", "Custom creative flight speed. CTRL + scroll to adjust.", List.of(speed, noDrag));
    }

    @Override
    protected void onMouseScrolled(double delta)
    {
        if (!KeyUtils.hasControlDown()) return;
        speed.setValue((float) Math.clamp(speed.getValue() + (delta * 0.025), speed.getDefault(), Float.MAX_VALUE));
        Msg.sendHud(String.format("Fly Speed: §a%.2f", speed.getValue()));
    }

    @Override
    protected void onDisabled()
    {
        mc.player.getAbilities().setFlyingSpeed(speed.getDefault());
    }
}
