package spctreutils.mixin.feature;

import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.impl.FlySpeed;
import spctreutils.module.feature.impl.SecondaryPlace;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    @Shadow
    public abstract Abilities getAbilities();

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo ci)
    {
        if (FeatureManager.isEnabled(FlySpeed.class))
            getAbilities().setFlyingSpeed(FlySpeed.SPEED.getValue());
    }

    @Inject(at = @At("HEAD"), method = "isSecondaryUseActive", cancellable = true)
    private void forceSecondaryPlaceAction(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureManager.isEnabled(SecondaryPlace.class))
            cir.setReturnValue(true);
    }
}