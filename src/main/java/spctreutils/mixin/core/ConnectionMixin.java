package spctreutils.mixin.core;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spctreutils.event.PacketEvent;

@Mixin(Connection.class)
public class ConnectionMixin
{
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;genericsFtw(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void onReceivedPacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci)
    {
        InteractionResult result = PacketEvent.RECEIVE.invoker().onReceive(packet);
        if (result == InteractionResult.FAIL)
            ci.cancel();
    }
}