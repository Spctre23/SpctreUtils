package spctreutils.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;

public interface MouseEvent
{
    Event<MouseEvent> SCROLL = EventFactory.createArrayBacked(
        MouseEvent.class,
        (listeners) -> (yOffset) ->
        {
            for (MouseEvent listener : listeners)
            {
                InteractionResult result = listener.onMouseScrolled(yOffset);
                if (result != InteractionResult.PASS) return result;
            }
            return InteractionResult.PASS;
        }
    );

    InteractionResult onMouseScrolled(double yOffset);
}
