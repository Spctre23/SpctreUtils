package spctreutils.mixin.feature;

import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spctreutils.module.feature.Feature;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.impl.FlySpeed;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    @Shadow
    public abstract Abilities getAbilities();

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo ci)
    {
        Feature feature = FeatureManager.getFeature(FlySpeed.class);
        if (feature == null || !feature.getEnabled())
        this.getAbilities().setFlyingSpeed(FlySpeed.speed.getValue());
    }
}