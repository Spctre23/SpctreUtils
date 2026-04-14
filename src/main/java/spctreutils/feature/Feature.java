package spctreutils.feature;

import com.mojang.blaze3d.platform.InputConstants;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import spctreutils.config.ConfigManager;
import spctreutils.config.ModConfig;
import spctreutils.key.Keybind;
import spctreutils.util.Msg;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Feature
{
    protected final Minecraft mc;
    protected final String name;
    private boolean enabled;
    private final String description;
    private final Keybind keybind;
    private final KEY_BEHAVIOR keyBehavior;
    private final Function<ModConfig, Boolean> configGetter;
    private final Consumer<Boolean> configSetter;

    protected enum KEY_BEHAVIOR
    {
        TOGGLE,
        TRIGGER
    }

    protected Feature(String name, String description, int keyCode, KEY_BEHAVIOR keyBehavior, Function<ModConfig, Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this.name = name;
        this.description = description;
        this.keybind = new Keybind(name, keyCode);
        this.keyBehavior = keyBehavior;
        this.configGetter = configGetter;
        this.configSetter = configSetter;
        this.enabled = configGetter.apply(ConfigManager.config);
        this.mc = Minecraft.getInstance();
        initialize();
    }

    protected Feature(String name, String description, KEY_BEHAVIOR keyBehavior, Function<ModConfig, Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this(name, description, InputConstants.UNKNOWN.getValue(), keyBehavior, configGetter, configSetter);
    }

    protected Feature(String name, String description, Function<ModConfig, Boolean> configGetter, Consumer<Boolean> configSetter)
    {
        this(name, description, KEY_BEHAVIOR.TOGGLE, configGetter, configSetter);
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

    protected void toggle() { setState(!enabled); }

    protected void setState(boolean state)
    {
        enabled = state;
        configSetter.accept(enabled);
        ConfigManager.save();
        onStateChanged();
    }

    public Option<Boolean> createOption()
    {
        return Option.<Boolean>createBuilder()
            .name(Component.literal(name))
            .description(OptionDescription.of(Component.literal(description)))
            .binding(false,
                () -> configGetter.apply(ConfigManager.config),
                v -> { configSetter.accept(v); ConfigManager.save(); })
            .controller(TickBoxControllerBuilder::create)
            .build();
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
        if (keyBehavior == KEY_BEHAVIOR.TOGGLE) sendToggleNotification();
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