package spctreutils.mixin.feature;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.impl.FlexiblePlace;
import spctreutils.module.feature.impl.NoPlaceRestrictions;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin
{
    @Inject(at = @At("HEAD"), method = "canPlace", cancellable = true)
    private void ignorePlacementRestrictions(BlockPlaceContext context, BlockState stateForPlacement, CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureManager.isEnabled(NoPlaceRestrictions.class))
            cir.setReturnValue(true);
    }

    @ModifyVariable(method = "placeBlock", at = @At("HEAD"), name = "placementState", argsOnly = true)
    protected BlockState placeBlock(BlockState placementState)
    {
        FlexiblePlace flexiblePlace = FeatureManager.getFeature(FlexiblePlace.class);
        if (flexiblePlace != null &&
            flexiblePlace.getEnabled() &&
            flexiblePlace.blockState != null &&
            flexiblePlace.flexiblePlaceModifier.isDown())
            placementState = flexiblePlace.blockState;
        return placementState;
    }
}