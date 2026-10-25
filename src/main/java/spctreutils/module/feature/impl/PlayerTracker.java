package spctreutils.module.feature.impl;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import spctreutils.helper.Visual.Msg;
import spctreutils.mixin.accessor.AzimuthWaypointInvoker;
import spctreutils.mixin.accessor.ChunkWaypointInvoker;
import spctreutils.mixin.accessor.Vec3iWaypointInvoker;
import spctreutils.module.feature.TriggerFeature;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.UUID;

public class PlayerTracker extends TriggerFeature
{
    private record Sample(double x, double z, double angle) {}

    private final HashMap<UUID, ArrayDeque<Sample>> waypointCache = new HashMap<>();

    public PlayerTracker()
    {
        super("Player Tracker", "Triangulates the estimated position of all players using the locator bar.");
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
                    Vec3 pos = vec3iWaypoint.invokePosition(mc.level, _ -> 0.0f);
                    sb.append("• Pos: " + Math.round(pos.x) + " " + Math.round(pos.y) + " " + Math.round(pos.z) + " (exact)");
                }
                case ChunkWaypointInvoker chunkWaypoint ->
                {
                    Vec3 pos = chunkWaypoint.invokePosition(mc.player.getY());
                    sb.append("• Pos: " + Math.round(pos.x) + " " + Math.round(pos.y) + " " + Math.round(pos.z) + " (chunk)");
                }
                case AzimuthWaypointInvoker _ ->
                {
                    ArrayDeque<Sample> e = waypointCache.get(uuid);
                    Vec2 pos = (e == null || e.size() < 2) ? null : getIntersectPos(e.peekFirst(), e.peekLast());

                    if (pos != null)
                        sb.append("• Pos: " + Math.round(pos.x) + " " + Math.round(pos.y) + " (approx)");
                    else
                        sb.append("• Pos: unknown (move sideways relative to the target to triangulate)");
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

    @Override
    protected void onTick()
    {
        mc.player.connection.getWaypointManager().forEachWaypoint(mc.player, waypoint ->
        {
            if (!(waypoint instanceof AzimuthWaypointInvoker azimuth)) return;
            UUID uuid = waypoint.id().left().orElse(null);
            if (uuid == null) return;

            ArrayDeque<Sample> samples = waypointCache.computeIfAbsent(uuid, k -> new ArrayDeque<>());

            Sample last = samples.peekLast();
            double x = mc.player.getX(), z = mc.player.getZ();
            if (last != null && Math.hypot(x - last.x(), z - last.z()) < 1.0) return;

            samples.addLast(new Sample(x, z, azimuth.invokeAngle()));
            if (samples.size() > 20) samples.pollFirst();
        });
    }

    @Nullable
    private Vec2 getIntersectPos(Sample a, Sample b)
    {
        double d1x = Math.cos(a.angle()), d1z = Math.sin(a.angle());
        double d2x = Math.cos(b.angle()), d2z = Math.sin(b.angle());

        double denom = d1x * d2z - d1z * d2x;
        if (Math.abs(denom) < 1e-3) return null;

        double t = ((b.x() - a.x()) * d2z - (b.z() - a.z()) * d2x) / denom;
        if (t < 0) return null;

        return new Vec2((float) (a.x() + t * d1x), (float) (a.z() + t * d1z));
    }
}
