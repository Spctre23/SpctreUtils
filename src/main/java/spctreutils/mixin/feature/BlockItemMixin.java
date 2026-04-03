package spctreutils.mixin.feature;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spctreutils.SpctreUtils;
import spctreutils.feature.impl.ForcePlace;
import spctreutils.feature.impl.NoClip;

@Mixin(BlockItem.class)
public class BlockItemMixin
{
	@Inject(at = @At("HEAD"), method = "canPlace", cancellable = true)
	private void canPlace(BlockPlaceContext blockPlaceContext, BlockState blockState, CallbackInfoReturnable<Boolean> cir)
	{
		if (SpctreUtils.instance == null) return;

		ForcePlace forcePlace = SpctreUtils.instance.getFeature(ForcePlace.class);
		if (SpctreUtils.config == null || forcePlace == null || !forcePlace.enabled) return;
		cir.setReturnValue(true);
	}
}