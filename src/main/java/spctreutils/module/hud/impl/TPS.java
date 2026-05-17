package spctreutils.module.hud.impl;

import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.world.InteractionResult;
import spctreutils.event.PacketEvent;
import spctreutils.module.hud.HudElement;

import java.awt.*;

public class TPS extends HudElement
{
    private long prevGameTime = -1;
    private long prevRealTime = -1;
    private double tps = 20.0;

    public TPS()
    {
        super("TPS", "Displays the server tps.");

        registerPacketListener();
    }

    private void registerPacketListener()
    {
        PacketEvent.RECEIVE.register(packet ->
        {
            if (packet instanceof ClientboundSetTimePacket gameTimePacket)
            {
                long currentGameTime = gameTimePacket.gameTime();
                long currentRealTime = System.currentTimeMillis();

                if (prevGameTime != -1)
                {
                    long gameDelta = currentGameTime - prevGameTime;
                    long realDelta = currentRealTime - prevRealTime;
                    if (realDelta > 0)
                        tps = Math.min((gameDelta * 1000.0) / realDelta, 20.0);
                }

                prevGameTime = currentGameTime;
                prevRealTime = currentRealTime;
            }

            return InteractionResult.PASS;
        });
    }

    @Override
    protected void onTick()
    {
        Color color = tps < 20 ? Color.RED : Color.GREEN;
        setText(String.format("%.1f", tps), color);
    }

    @Override
    protected void onDisconnect() { reset(); }

    @Override
    protected void onJoin() { reset(); }

    private void reset()
    {
        prevGameTime = -1;
        prevRealTime = -1;
        tps = 20.0;
    }
}
