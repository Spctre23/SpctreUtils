package spctreutils.mixin.feature;

import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spctreutils.config.ConfigManager;

@Mixin(Player.class)
public abstract class PlayerMixin
{
    @Shadow
    public abstract Abilities getAbilities();

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void tick(CallbackInfo ci)
    {
        if (ConfigManager.config == null) return;
        this.getAbilities().setFlyingSpeed(ConfigManager.config.feature.flyingSpeed);
    }
}