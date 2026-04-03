package spctreutils.feature;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import spctreutils.SpctreUtils;
import spctreutils.key.Keybind;
import spctreutils.utils.Msg;

public abstract class Feature
{
    public final String name;
    public final String description;
    public boolean enabled;
    protected final Minecraft mc;
    private final Keybind keybind;
    private final boolean chatFeedback;

    public Feature(String name, String description, boolean enabled, boolean chatFeedback)
    {
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.chatFeedback = chatFeedback;
        this.keybind = new Keybind(name, InputConstants.UNKNOWN.getValue());
        this.mc = Minecraft.getInstance();

        registerKeybind();
    }

    public Feature(String name, String description, boolean enabled, boolean chatFeedback, int keyCode)
    {
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.chatFeedback = chatFeedback;
        this.keybind = new Keybind(name, keyCode);
        this.mc = Minecraft.getInstance();

        registerKeybind();
    }

    public void onEnable() {}

    public void onDisable() {}

    public void onTick() {}

    public void toggle()
    {
        enabled = !enabled;
        if (enabled)
        {
            onEnable();
            ClientTickEvents.END_CLIENT_TICK.register(mc ->
            {
                if (!this.enabled) return;
                onTick();
            });
        }
        else onDisable();

        if (chatFeedback)
            sendToggleNotification();
    }

    private void sendToggleNotification()
    {
        Msg.sendHud(this.enabled ? "§a" + this.name + " = ON" : "§c" + this.name + " = OFF");
    }

    private void registerKeybind()
    {
        keybind.onPressed(() -> toggle());
    }
}
