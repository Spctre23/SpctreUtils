package spctreutils.feature;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import spctreutils.SpctreUtils;
import spctreutils.config.ConfigManager;
import spctreutils.config.ModConfig;
import spctreutils.key.Keybind;
import spctreutils.util.Msg;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Feature
{
    protected final Minecraft mc;
    private boolean enabled;
    private final String name;
    private final String description;
    private final Keybind keybind;
    private final KEY_BEHAVIOR keyBehavior;
    private final boolean chatFeedback;
    private final Function<ModConfig, Boolean> configGetter;
    private final Consumer<Boolean> configSetter;

    protected enum KEY_BEHAVIOR
    {
        TOGGLE,
        TRIGGER
    }

    protected Feature(String name, String description, boolean chatFeedback, int keyCode, KEY_BEHAVIOR keyBehavior,
                      Function<ModConfig, Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this.name = name;
        this.description = description;
        this.chatFeedback = chatFeedback;
        this.keybind = new Keybind(name, keyCode);
        this.mc = Minecraft.getInstance();
        this.keyBehavior = keyBehavior;
        this.configGetter = configGetter;
        this.configSetter = configSetter;
        this.enabled = configGetter.apply(ConfigManager.config);

        initialize();
    }

    protected Feature(String name, String description, boolean chatFeedback, int keyCode,
                      Function<ModConfig, Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this(name, description, chatFeedback, keyCode, KEY_BEHAVIOR.TOGGLE, configGetter, configSetter);
    }

    protected Feature(String name, String description, boolean chatFeedback,
                      Function<ModConfig, Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this(name, description, chatFeedback, InputConstants.UNKNOWN.getValue(), configGetter, configSetter);
    }

    protected Feature(String name, String description,
                      Function<ModConfig, Boolean> configGetter, Consumer<Boolean> configSetter)
{
        this(name, description, true, configGetter, configSetter);
    }

    protected Feature(String name, Function<ModConfig, Boolean> configGetter, Consumer<Boolean> configSetter)
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

    private void initialize()
    {
        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            syncFromConfig();
            if (enabled && client.player != null) onTick();
        });

        registerKeybind();
    }

    private void syncFromConfig()
    {
        boolean configValue = configGetter.apply(ConfigManager.config);
        if (configValue == enabled) return;
        enabled = configValue;
        onStateChanged();
    }

    private void onStateChanged()
    {
        if (enabled) onEnabled();
        else onDisabled();
        if (chatFeedback) sendToggleNotification();
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
