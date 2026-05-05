package spctreutils.key;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class Keybind
{
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.withDefaultNamespace("SpctreUtils"));
    private final KeyMapping key;
    private boolean wasDown = false;

    public Keybind(String name, int keyCode)
    {
        key = KeyBindingHelper.registerKeyBinding(new KeyMapping(name, keyCode, CATEGORY));
    }

    public void onPressed(Runnable pressedAction)
    {
        ClientTickEvents.END_CLIENT_TICK.register(mc ->
        {
            while (key.consumeClick()) pressedAction.run();
        });
    }

    public void onReleased(Runnable releasedAction)
    {
        ClientTickEvents.END_CLIENT_TICK.register(mc ->
        {
            boolean isDown = key.isDown();
            if (wasDown && !isDown) releasedAction.run();
            wasDown = isDown;
        });
    }
}
