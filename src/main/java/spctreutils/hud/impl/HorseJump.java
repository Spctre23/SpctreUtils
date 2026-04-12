package spctreutils.hud.impl;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import spctreutils.hud.HudElement;

public class HorseJump extends HudElement
{
    public HorseJump()
    {
        super("Horse Jump", config -> config.hud.horseJump);
    }

    @Override
    public void onTick()
    {
        if (mc.crosshairPickEntity instanceof Horse horse)
        {
            String jumpHeight = String.format("%.3f m", calculateJumpHeight(horse.getAttributeValue(Attributes.JUMP_STRENGTH)));
            setText(jumpHeight);
            return;
        }
        removeContent();
    }

    // Calculations obtained from MiniHud
    // https://github.com/sakura-ryoko/minihud/blob/5107603e8c7eced6f75732282d21fa9dbea37551/src/main/java/fi/dy/masa/minihud/info/entity/InfoLineHorseJump.java#L53
    private double calculateJumpHeight(double jumpStrength)
    {
        double jumpVelocity = -0.09333 * jumpStrength * jumpStrength + 1.05367 * jumpStrength + 0.01177;
        return (jumpVelocity * jumpVelocity) / (2.0 * LivingEntity.DEFAULT_BASE_GRAVITY);
    }
}
