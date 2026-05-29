package spctreutils.module.feature.impl;

import dev.isxander.yacl3.gui.utils.KeyUtils;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import spctreutils.helper.Msg;
import spctreutils.module.feature.Feature;
import spctreutils.setting.Setting;

import java.util.List;

public class FlySpeed extends Feature
{
    public static final Setting<Float> speed = new Setting<>("Speed", 0.1f, Float.class);
    public static final Setting<Boolean> noGlide = new Setting<>("No Glide", false, Boolean.class);

    public FlySpeed()
    {
        super("Fly Speed", "Custom creative flight speed. CTRL + scroll to adjust.", List.of(speed, noGlide));
    }

    @Override
    protected void onMouseScrolled(double delta)
    {
        if (!KeyUtils.hasControlDown()) return;
        speed.setValue((float) Math.clamp(speed.getValue() + (delta * 0.025), speed.getDefault(), Float.MAX_VALUE));
        Msg.sendHud(String.format("Fly Speed: §a%.2f", speed.getValue()));
    }

    @Override
    protected void onTick()
    {
        if (!mc.player.getAbilities().flying || !noGlide.getValue()) return;

        Vec2 moveInput = mc.player.input.getMoveVector();
        boolean horizontalInput = moveInput.x != 0 || moveInput.y != 0;

        ClientInput input = mc.player.input;
        boolean verticalInput = input.keyPresses.jump() || input.keyPresses.shift();

        Vec3 vel = mc.player.getDeltaMovement();
        double newX = horizontalInput ? vel.x : 0;
        double newY = verticalInput ? vel.y : 0;
        double newZ = horizontalInput ? vel.z : 0;

        mc.player.setDeltaMovement(newX, newY, newZ);
    }

    @Override
    protected void onDisabled()
    {
        mc.player.getAbilities().setFlyingSpeed(speed.getDefault());
    }
}
