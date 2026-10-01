package spctreutils.mixin.feature;

import com.mojang.patchy.BlockedServers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.impl.BypassServerBlacklist;

@Mixin(BlockedServers.class)
public abstract class BlockedServersMixin
{
    @Inject(method = "isBlockedServerHostName", at = @At("HEAD"), cancellable = true)
    private void bypassServerBlacklist(String server, CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureManager.isEnabled(BypassServerBlacklist.class))
            cir.setReturnValue(false);
    }
}