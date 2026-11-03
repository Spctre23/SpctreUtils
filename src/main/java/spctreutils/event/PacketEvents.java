package spctreutils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionResult;

public final class PacketEvents
{
    private PacketEvents() {}

    @FunctionalInterface
    public interface Receive
    {
        InteractionResult onReceive(Packet<?> packet);
    }

    @FunctionalInterface
    public interface Send
    {
        InteractionResult onSend(Packet<?> packet);
    }

    public static final Event<Receive> RECEIVE = EventFactory.createArrayBacked(
        Receive.class,
        listeners -> packet ->
        {
            for (Receive listener : listeners)
            {
                InteractionResult result = listener.onReceive(packet);
                if (result != InteractionResult.PASS) return result;
            }
            return InteractionResult.PASS;
        }
    );

    public static final Event<Send> SEND = EventFactory.createArrayBacked(
        Send.class,
        listeners -> packet ->
        {
            for (Send listener : listeners)
            {
                InteractionResult result = listener.onSend(packet);
                if (result != InteractionResult.PASS) return result;
            }
            return InteractionResult.PASS;
        }
    );
}