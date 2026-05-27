package spctreutils.mixin.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.impl.FlySpeed;

@Mixin(LivingEntity.class)
public class LivingEntityMixin
{
    @ModifyVariable(
        method = "travelInAir",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"
        ),
        ordinal = 1
    )
    private float handleRelativeFrictionAndCalculateMovement(float drag)
    {
        if (FeatureManager.isEnabled(FlySpeed.class))
        {
            Minecraft mc = Minecraft.getInstance();
            if (FlySpeed.noDrag.getValue() && mc.player != null && mc.player.getAbilities().flying && !mc.player.onGround())
            {
                drag = 0.0f;
            }
        }
        return drag;
    }
}
