package spctreutils.mixin.core;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spctreutils.event.MouseEvent;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin
{
    @Inject(method = "onScroll", at = @At("HEAD"))
    private void onScroll(long window, double xOffset, double yOffset, CallbackInfo ci)
    {
        MouseEvent.SCROLL.invoker().onMouseScrolled(yOffset);
    }
}
