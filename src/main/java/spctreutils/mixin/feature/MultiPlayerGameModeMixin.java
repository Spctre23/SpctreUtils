package spctreutils.mixin.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spctreutils.helper.Placement.PendingPlacement;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.impl.FlexiblePlace;
import spctreutils.module.feature.impl.NoBreakDelay;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin
{
    @Shadow
    private int destroyDelay;

    @Redirect(method = "continueDestroyBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyDelay:I", opcode = Opcodes.PUTFIELD))
    private void removeBlockBreakDelay(MultiPlayerGameMode instance, int value)
    {
        if (FeatureManager.isEnabled(NoBreakDelay.class))
        {
            destroyDelay = 0;
            return;
        }
        destroyDelay = value;
    }

    @Inject(method = "useItemOn", at = @At("HEAD"))
    private void prepareFlexiblePlacement(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir)
    {
        PendingPlacement.clear();
        if (Minecraft.getInstance().getSingleplayerServer() != null) return;

        FlexiblePlace fp = FeatureManager.getFeature(FlexiblePlace.class);
        ItemStack stack = player.getItemInHand(hand);
        if (fp == null || !(stack.getItem() instanceof BlockItem item)) return;

        BlockState override = fp.getOverrideState(item.getBlock());
        if (override == null) return;

        BlockPlaceContext ctx = item.updatePlacementContext(new BlockPlaceContext(player, hand, stack, blockHit));
        if (ctx == null) return;

        PendingPlacement.state = override;
        PendingPlacement.placementPos = ctx.getClickedPos();
    }

    @Inject(method = "useItemOn", at = @At("RETURN"))
    private void discardFlexiblePlacement(LocalPlayer player, InteractionHand hand, BlockHitResult blockHit, CallbackInfoReturnable<InteractionResult> cir)
    {
        PendingPlacement.clear();
    }
}
