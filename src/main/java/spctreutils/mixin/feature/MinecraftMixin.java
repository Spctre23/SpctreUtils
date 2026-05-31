package spctreutils.mixin.feature;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.impl.FastUse;

@Mixin(Minecraft.class)
public class MinecraftMixin
{
    @Shadow private int rightClickDelay;

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void removeRightClickDelay(CallbackInfo info)
    {
        if (FeatureManager.isEnabled(FastUse.class))
            rightClickDelay = 0;
    }
}
