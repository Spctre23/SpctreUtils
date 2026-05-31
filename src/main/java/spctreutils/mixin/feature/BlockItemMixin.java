package spctreutils.mixin.feature;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.impl.ForcePlace;

@Mixin(BlockItem.class)
public class BlockItemMixin
{
    @Inject(at = @At("HEAD"), method = "canPlace", cancellable = true)
    private void canPlace(BlockPlaceContext blockPlaceContext, BlockState blockState, CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureManager.isEnabled(ForcePlace.class))
            cir.setReturnValue(true);
    }
}