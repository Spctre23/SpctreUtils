package spctreutils.mixin.core;

import net.minecraft.client.MouseHandler;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spctreutils.event.MouseEvent;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin
{
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onScroll(long handle, double xoffset, double yoffset, CallbackInfo ci)
    {
        InteractionResult result = MouseEvent.SCROLL.invoker().onMouseScrolled(yoffset);
        if (result != InteractionResult.PASS) ci.cancel();
    }
}
