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
    public final String name;
    public final String description;
    protected final Minecraft mc;
    protected final KEY_BEHAVIOR keyBehavior;
    protected boolean enabled;
    private final Keybind keybind;
    private final boolean chatFeedback;
    private final Consumer<Boolean> configSetter;

    protected enum KEY_BEHAVIOR
    {
        TOGGLE,
        TRIGGER
    }

    public Feature(String name, String description, boolean chatFeedback, int keyCode, KEY_BEHAVIOR keyBehavior,
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

    public Feature(String name, String description, boolean chatFeedback, int keyCode,
                   Supplier<Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this(name, description, chatFeedback, keyCode, KEY_BEHAVIOR.TOGGLE, configGetter, configSetter);
    }

    public Feature(String name, String description, boolean chatFeedback,
                   Supplier<Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this(name, description, chatFeedback, InputConstants.UNKNOWN.getValue(), configGetter, configSetter);
    }

    public Feature(String name, String description,
                   Supplier<Boolean> configGetter, Consumer<Boolean> configSetter)
{
        this(name, description, true, configGetter, configSetter);
    }

    public Feature(String name, Supplier<Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this(name, "", configGetter, configSetter);
    }

    public void onEnable() {}

    public void onDisable() {}

    public void onKeyPressed() {}

    public void onKeyReleased() {}

    public void onTick() {}

    public void toggle()
    {
        enabled = !enabled;
        setState();

        configSetter.accept(enabled);
        ConfigManager.save();
    }

    private void setState()
    {
        if (enabled) onEnable();
        else onDisable();
        if (chatFeedback) sendToggleNotification();
    }

    public void syncFromConfig(boolean newValue)
    {
        if (newValue == enabled) return;
        enabled = newValue;
        setState();
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
