package spctreutils.mixin.feature;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.impl.BypassChatRestrictions;
import spctreutils.module.feature.impl.BypassMultiplayerRestrictions;

@Mixin(Minecraft.class)
public class MinecraftMixin
{
    @Inject(method = "allowsMultiplayer", at = @At("HEAD"), cancellable = true)
    private void bypassMultiplayerRestrictions(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureManager.isEnabled(BypassMultiplayerRestrictions.class))
            cir.setReturnValue(true);
    }

    @Inject(method = "allowsRealms", at = @At("HEAD"), cancellable = true)
    private void bypassMultiplayerRestrictions2(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureManager.isEnabled(BypassMultiplayerRestrictions.class))
            cir.setReturnValue(true);
    }

    @Inject(method = "isTextFilteringEnabled", at = @At("HEAD"), cancellable = true)
    private void bypassProfanityFiltering(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureManager.isEnabled(BypassChatRestrictions.class) && BypassChatRestrictions.NO_PROFANITY_FILTER.getValue())
            cir.setReturnValue(true);
    }
}
