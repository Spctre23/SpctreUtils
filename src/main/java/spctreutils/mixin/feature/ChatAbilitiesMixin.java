package spctreutils.mixin.feature;

import net.minecraft.client.multiplayer.chat.ChatAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.impl.BypassChatRestrictions;

@Mixin(ChatAbilities.Builder.class)
public class ChatAbilitiesMixin
{
    @Inject(method = "addRestriction", at = @At("HEAD"), cancellable = true)
    private void bypassChatRestrictions(CallbackInfoReturnable<Boolean> cir)
    {
        if (FeatureManager.isEnabled(BypassChatRestrictions.class))
            cir.cancel();
    }
}
