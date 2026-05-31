package spctreutils.module.feature.impl;

import dev.isxander.yacl3.gui.utils.KeyUtils;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import spctreutils.component.TextComp;
import spctreutils.helper.MathHelper;
import spctreutils.helper.Msg;
import spctreutils.module.feature.Feature;
import spctreutils.setting.Setting;

import java.awt.*;
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

        float flySpeed = (float) MathHelper.round(Math.clamp(speed.getValue() + (delta * 0.01), 0.0, Float.MAX_VALUE), 3);
        speed.setValue(flySpeed);

        Color color = flySpeed >= speed.getDefault() ? Color.GREEN : Color.YELLOW;
        Msg.sendHud(new TextComp("Fly Speed: "), new TextComp(String.format("%.2f", flySpeed), color));
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

        if (horizontalInput)
        {
            float tickRate = mc.level.tickRateManager().tickrate();
            float speed = mc.player.getAbilities().getFlyingSpeed() * tickRate;
            float yaw = mc.player.getYRot() * ((float) Math.PI / 180f);

            Vec3 forward = new Vec3(-Math.sin(yaw), 0, Math.cos(yaw));
            Vec3 right = new Vec3(Math.cos(yaw), 0, Math.sin(yaw));

            Vec3 direction = forward.scale(moveInput.y).add(right.scale(moveInput.x)).normalize();

            vel = new Vec3(direction.x * speed, vel.y, direction.z * speed);
        }
        else
            vel = new Vec3(0, vel.y, 0);

        if (verticalInput)
        {
            float speed = mc.player.getAbilities().getFlyingSpeed() * 20;
            double yVel = input.keyPresses.jump() ? speed : -speed;
            vel = new Vec3(vel.x, yVel, vel.z);
        }
        else
            vel = new Vec3(vel.x, 0, vel.z);

        mc.player.setDeltaMovement(vel);
    }

    @Override
    protected void onDisabled()
    {
        mc.player.getAbilities().setFlyingSpeed(speed.getDefault());
    }
}
