package spctreutils.mixin.feature;

import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spctreutils.SpctreUtils;
import spctreutils.config.ModConfig;

@Mixin(Player.class)
public abstract class PlayerMixin
{
	@Shadow public abstract Abilities getAbilities();

	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	private void tick(CallbackInfo ci)
	{
		if (SpctreUtils.config == null) return;
		this.getAbilities().setFlyingSpeed(SpctreUtils.config.flyingSpeed);
	}
}