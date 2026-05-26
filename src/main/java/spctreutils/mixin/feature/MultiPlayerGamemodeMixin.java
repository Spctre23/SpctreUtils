package spctreutils.mixin.feature;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.impl.NoBreakDelay;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGamemodeMixin
{
    @Shadow private int destroyDelay;

    @Redirect(method = "continueDestroyBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;destroyDelay:I", opcode = Opcodes.PUTFIELD))
    private void continueDestroyBlock(MultiPlayerGameMode interactionManager, int value)
    {
        if (FeatureManager.isEnabled(NoBreakDelay.class))
        {
            destroyDelay = 0;
            return;
        }

        destroyDelay = value;
    }
}
