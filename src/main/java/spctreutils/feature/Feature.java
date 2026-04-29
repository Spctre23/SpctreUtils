package spctreutils.feature;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import spctreutils.config.ConfigManager;
import spctreutils.config.OptionProvider;
import spctreutils.key.Keybind;
import spctreutils.setting.Setting;
import spctreutils.helper.Msg;

import java.util.List;

public abstract class Feature implements OptionProvider
{
    protected final Minecraft mc;
    protected final String name;
    protected final List<Setting<?>> settings;
    private boolean enabled;
    private final String description;
    private final Keybind keybind;
    private final KEY_BEHAVIOR keyBehavior;

    protected enum KEY_BEHAVIOR
    {
        TOGGLE,
        TRIGGER
    }

    protected Feature(String name, String description, int keyCode, KEY_BEHAVIOR keyBehavior, List<Setting<?>> settings)
    {
        this.name = name;
        this.description = description;
        this.keybind = new Keybind(name, keyCode);
        this.keyBehavior = keyBehavior;
        this.settings = settings;
        this.mc = Minecraft.getInstance();
        this.enabled = getConfigValue();

        for (Setting<?> setting : settings)
            setting.setKey(name);

        registerEvents();
    }

    protected Feature(String name, String description, KEY_BEHAVIOR keyBehavior, List<Setting<?>> settings)
    {
        this(name, description, InputConstants.UNKNOWN.getValue(), keyBehavior, settings);
    }

    protected Feature(String name, String description, KEY_BEHAVIOR keyBehavior)
    {
        this(name, description, keyBehavior, List.of());
    }

    protected Feature(String name, String description, List<Setting<?>> settings)
    {
        this(name, description, KEY_BEHAVIOR.TOGGLE, settings);
    }

    protected Feature(String name, String description)
    {
        this(name, description, List.of());
    }

    protected Feature(String name)
    {
        this(name, "");
    }

    @Override public String getName() { return name; }

    @Override public String getDescription() { return description; }

    @Override public boolean getEnabled() { return enabled; }

    @Override public void setEnabled(boolean value) { setState(value); }

    @Override public List<Setting<?>> getSettings() { return settings; }

    protected void onEnabled() {}

    protected void onDisabled() {}

    protected void onKeyPressed() {}

    protected void onKeyReleased() {}

    protected void onTick() {}

    protected void onRender(WorldRenderContext context) {}

    protected void toggle() { setState(!enabled); }

    protected void setState(boolean state)
    {
        enabled = state;
        setConfigValue(enabled);
        onStateChanged();
    }

    public boolean isEnabled() { return enabled; }

    private boolean getConfigValue()
    {
        return ConfigManager.config.featureStates.getOrDefault(getClass().getSimpleName(), false);
    }

    private void setConfigValue(boolean value)
    {
        ConfigManager.config.featureStates.put(getClass().getSimpleName(), value);
        ConfigManager.save();
    }

    private void syncFromConfig()
    {
        boolean configValue = getConfigValue();
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

    private void registerEvents()
    {
        ClientTickEvents.START_CLIENT_TICK.register(mc ->
        {
            syncFromConfig();
            if (enabled && mc.level != null && mc.player != null) onTick();
        });
        WorldRenderEvents.AFTER_TRANSLUCENT.register(context ->
        {
            if (enabled && mc.level != null && mc.player != null) onRender(context);
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

        if (keyBehavior == KEY_BEHAVIOR.TRIGGER)
            keybind.onReleased(() ->
            {
                if (enabled) onKeyReleased();
            });
    }

    private void sendToggleNotification()
    {
        Msg.sendHud(enabled ? "§a" + name + " = ON" : "§c" + name + " = OFF");
    }
}