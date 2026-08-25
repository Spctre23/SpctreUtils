package spctreutils.mixin.core;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spctreutils.SpctreUtils;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin
{
    @Inject(at = @At("TAIL"), method = "placeNewPlayer")
    private void onPlayerJoin(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci)
    {
        SpctreUtils.serverPlayers.put(player.getUUID(), player);
    }

    @Inject(at = @At("HEAD"), method = "remove")
    private void onPlayerLeave(ServerPlayer player, CallbackInfo ci)
    {
        SpctreUtils.serverPlayers.remove(player.getUUID());
        if (SpctreUtils.serverPlayer == player)
            SpctreUtils.serverPlayer = null;
    }
}