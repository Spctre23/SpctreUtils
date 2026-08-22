package spctreutils.module.feature.impl;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.phys.Vec3;
import spctreutils.helper.Visual.Msg;
import spctreutils.mixin.accessor.AzimuthWaypointInvoker;
import spctreutils.mixin.accessor.ChunkWaypointInvoker;
import spctreutils.mixin.accessor.Vec3iWaypointInvoker;
import spctreutils.module.feature.TriggerFeature;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class PlayerTracker extends TriggerFeature
{
    public PlayerTracker()
    {
        super("Player Tracker", """
                Prints the estimated position of all players on the server (if possible).
                
                To reliably track player coordinates from any distance, you must trigger PlayerTracker within simulation distance of your target(s) at least once.""");
    }

    @Override
    protected void onKeyPressed()
    {
        mc.player.connection.getWaypointManager().forEachWaypoint(mc.player, waypoint ->
        {
            UUID uuid = waypoint.id().left().orElse(null);
            if (uuid == null) return;

            ClientPacketListener connection = mc.getConnection();
            if (connection == null) return;

            PlayerInfo playerInfo = connection.getPlayerInfo(uuid);
            if (playerInfo == null) return;
            String name = playerInfo.getProfile().name();

            StringBuilder sb = new StringBuilder();
            sb.append("\n" + name + ":\n");

            switch (waypoint)
            {
                case Vec3iWaypointInvoker vec3iWaypoint ->
                {
                    Vec3 pos = vec3iWaypoint.invokePosition(mc.level, entity -> 0.0f);
                    sb.append("• Pos: " + Math.round(pos.x) + " " + Math.round(pos.y) + " " + Math.round(pos.z) + " (exact)");
                }
                case ChunkWaypointInvoker chunkWaypoint ->
                {
                    Vec3 pos = chunkWaypoint.invokePosition(mc.player.getY());
                    sb.append("• Pos: " + Math.round(pos.x) + " " + Math.round(pos.y) + " " + Math.round(pos.z) + " (chunk)");
                }
                case AzimuthWaypointInvoker azimuthWaypoint ->
                {
                    BigDecimal angle = new BigDecimal(azimuthWaypoint.invokeAngle() * (180.0f / (float) Math.PI)).setScale(2, RoundingMode.HALF_UP);
                    sb.append("• Angle: " + angle + "°");
                }
                default ->
                {
                }
            }

            long distance = Math.round(Math.sqrt(waypoint.distanceSquared(mc.player)));
            String distStr = distance == Long.MAX_VALUE ? "unknown" : distance + " blocks";
            sb.append("\n• Distance: " + distStr);
            Msg.sendChat(sb.toString());
        });
    }
}
