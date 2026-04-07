package spctreutils.feature;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import spctreutils.config.ConfigManager;
import spctreutils.key.Keybind;
import spctreutils.util.Msg;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Feature
{
    protected final Minecraft mc;
    private boolean enabled;
    private final String name;
    private final String description;
    private final Keybind keybind;
    private final KEY_BEHAVIOR keyBehavior;
    private final boolean chatFeedback;
    private final Consumer<Boolean> configSetter;

    protected enum KEY_BEHAVIOR
    {
        TOGGLE,
        TRIGGER
    }

    protected Feature(String name, String description, boolean chatFeedback, int keyCode, KEY_BEHAVIOR keyBehavior,
                   Supplier<Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this.name = name;
        this.description = description;
        this.chatFeedback = chatFeedback;
        this.keybind = new Keybind(name, keyCode);
        this.mc = Minecraft.getInstance();
        this.keyBehavior = keyBehavior;
        this.configSetter = configSetter;
        this.enabled = configGetter.get();

        initialize();
    }

    protected Feature(String name, String description, boolean chatFeedback, int keyCode,
                   Supplier<Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this(name, description, chatFeedback, keyCode, KEY_BEHAVIOR.TOGGLE, configGetter, configSetter);
    }

    protected Feature(String name, String description, boolean chatFeedback,
                   Supplier<Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this(name, description, chatFeedback, InputConstants.UNKNOWN.getValue(), configGetter, configSetter);
    }

    protected Feature(String name, String description,
                   Supplier<Boolean> configGetter, Consumer<Boolean> configSetter)
{
        this(name, description, true, configGetter, configSetter);
    }

    protected Feature(String name, Supplier<Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this(name, "", configGetter, configSetter);
    }

    protected void onEnabled() {}

    protected void onDisabled() {}

    protected void onKeyPressed() {}

    protected void onKeyReleased() {}

    protected void onTick() {}

    protected void toggle()
    {
        setState(!enabled);
    }

    protected void setState(boolean state)
    {
        enabled = state;
        configSetter.accept(enabled);
        ConfigManager.save();

        onStateChanged();
    }

    public void syncFromConfig(boolean newValue)
    {
        if (newValue == enabled) return;
        enabled = newValue;

        onStateChanged();
    }

    private void onStateChanged()
    {
        if (enabled) onEnabled();
        else onDisabled();
        if (chatFeedback) sendToggleNotification();
    }

    private void initialize()
    {
        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            if (enabled && client.player != null) onTick();
        });

        registerKeybind();
    }

    private void registerKeybind()
    {
        keybind.onPressed(() ->
        {
            if (keyBehavior == KEY_BEHAVIOR.TOGGLE)
            {
                toggle();
                return;
            }

            if (enabled) onKeyPressed();
        });

        if (keyBehavior == KEY_BEHAVIOR.TRIGGER && enabled)
            keybind.onReleased(() -> onKeyReleased());
    }

    private void sendToggleNotification()
    {
        Msg.sendHud(enabled ? "§a" + name + " = ON" : "§c" + name + " = OFF");
    }
}
