package spctreutils.mixin.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spctreutils.SpctreUtils;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin
{
    @Inject(at = @At("TAIL"), method = "handleLogin")
    private void onLogin(ClientboundLoginPacket clientboundLoginPacket, CallbackInfo ci)
    {
        initializeServerPlayer();
    }

    @Inject(at = @At("TAIL"), method = "handleRespawn")
    private void onRespawn(ClientboundRespawnPacket clientboundRespawnPacket, CallbackInfo ci)
    {
        initializeServerPlayer();
    }

    private void initializeServerPlayer()
    {
        SpctreUtils.localPlayer = Minecraft.getInstance().player;
        if (SpctreUtils.localPlayer == null || SpctreUtils.serverPlayer != null) return;
        SpctreUtils.serverPlayer = SpctreUtils.serverPlayers.get(SpctreUtils.localPlayer.getUUID());
    }
}