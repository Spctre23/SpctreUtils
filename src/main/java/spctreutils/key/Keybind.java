package spctreutils.key;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class Keybind
{
    public KeyMapping key;
    private boolean wasDown = false;

    public Keybind(String name, int keyCode)
    {
        key = KeyBindingHelper.registerKeyBinding(new KeyMapping(name, keyCode, "SpctreUtils"));
    }

    public void onPressed(Runnable pressedAction)
    {
        ClientTickEvents.END_CLIENT_TICK.register(mc ->
        {
            while (key.consumeClick()) pressedAction.run();
        });
    }

    public void onHeld(Runnable onHeld)
    {
        ClientTickEvents.END_CLIENT_TICK.register(mc ->
        {
            if (key.isDown()) onHeld.run();
        });
    }

    public void onReleased(Runnable releasedAction)
    {
        boolean isDown = key.isDown();
        if (wasDown && !isDown) releasedAction.run();
        wasDown = isDown;
    }
}
