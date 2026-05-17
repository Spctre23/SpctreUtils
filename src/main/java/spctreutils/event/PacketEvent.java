package spctreutils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionResult;

public interface PacketEvent
{
    Event<PacketEvent> RECEIVE = EventFactory.createArrayBacked(
        PacketEvent.class,
        (listeners) -> (packet) ->
        {
            for (PacketEvent listener : listeners)
            {
                InteractionResult result = listener.onReceive(packet);
                if (result != InteractionResult.PASS) return result;
            }
            return InteractionResult.PASS;
        }
    );

    InteractionResult onReceive(Packet<?> packet);
}
