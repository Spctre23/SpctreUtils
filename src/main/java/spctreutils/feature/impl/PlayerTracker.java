package spctreutils.feature.impl;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.phys.Vec3;
import spctreutils.feature.Feature;
import spctreutils.mixin.accessor.AzimuthWaypointInvoker;
import spctreutils.mixin.accessor.ChunkWaypointInvoker;
import spctreutils.mixin.accessor.Vec3iWaypointInvoker;
import spctreutils.helper.Msg;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class PlayerTracker extends Feature
{
    public PlayerTracker()
    {
        super("Player Tracker", "Prints the estimated position of all players on the server (if possible).", KEY_BEHAVIOR.TRIGGER);
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
            String name = playerInfo.getProfile().getName();

            StringBuilder sb = new StringBuilder();
            sb.append("\n" + name + ":\n");

            if (waypoint instanceof Vec3iWaypointInvoker vec3iWaypoint)
            {
                Vec3 pos = vec3iWaypoint.invokePosition(mc.level);
                sb.append("• Pos: " + Math.round(pos.x) + " " + Math.round(pos.y) + " " + Math.round(pos.z) + " (exact)");
            }
            else if (waypoint instanceof ChunkWaypointInvoker chunkWaypoint)
            {
                Vec3 pos = chunkWaypoint.invokePosition(mc.player.getY());
                sb.append("• Pos: " + Math.round(pos.x) + " " + Math.round(pos.y) + " " + Math.round(pos.z) + " (chunk)");
            }
            else if (waypoint instanceof AzimuthWaypointInvoker azimuthWaypoint)
            {
                BigDecimal angle = new BigDecimal(azimuthWaypoint.invokeAngle() * (180.0f / (float) Math.PI)).setScale(2, RoundingMode.HALF_UP);
                sb.append("• Angle: " + angle + "°");
            }

            long distance = Math.round(Math.sqrt(waypoint.distanceSquared(mc.player)));
            String distStr = distance == Long.MAX_VALUE ? "unknown" : distance + " blocks";
            sb.append("\n• Distance: " + distStr);
            Msg.sendChat(sb.toString());
        });
    }
}
