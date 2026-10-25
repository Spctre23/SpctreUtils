package spctreutils.module;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import spctreutils.config.yacl.OptionProvider;
import spctreutils.setting.Setting;

import java.util.ArrayList;
import java.util.List;

public abstract class Module implements OptionProvider
{
    protected final Minecraft mc;
    private final String name;
    private final String description;
    private List<Setting<?>> settings;
    private boolean enabled;

    protected Module(String name, String description)
    {
        this.name = name;
        this.description = description;
        this.mc = Minecraft.getInstance();
        this.enabled = getConfigValue();

        registerEvents();
    }

    protected Module(String name)
    {
        this(name, "");
    }

    @Override public String getName() { return name; }

    @Override public String getDescription() { return description; }

    @Override public List<Setting<?>> getSettings() { return settings; }

    @Override public boolean isEnabled() { return enabled; }

    @Override
    public void setState(boolean state)
    {
        enabled = state;
        setConfigValue(enabled);
        onStateChanged();
    }

    protected void onEnabled() {}

    protected void onDisabled() {}

    protected void onTick() {}

    protected void onJoin() {}

    protected void onDisconnect() {}

    protected void dispose() {}

    protected abstract boolean getConfigValue();

    protected abstract void setConfigValue(boolean value);

    protected void syncFromConfig()
    {
        boolean configValue = getConfigValue();
        if (configValue == enabled) return;
        enabled = configValue;
        onStateChanged();
    }

    protected void onStateChanged()
    {
        if (enabled) onEnabled();
        else
        {
            onDisabled();
            dispose();
        }
    }

    protected void registerSettings(List<Setting<?>> moduleSettings)
    {
        registerSettings(new ArrayList<>(), moduleSettings);
    }

    protected void registerSettings(List<Setting<?>> baseSettings, List<Setting<?>> moduleSettings)
    {
        settings = baseSettings;
        settings.addAll(moduleSettings);
        for (Setting<?> setting : settings)
            setting.setKey(name);
    }

    protected void registerEvents()
    {
        ClientTickEvents.START_CLIENT_TICK.register(mc ->
        {
            syncFromConfig();
            if (enabled && mc.level != null && mc.player != null) onTick();
        });
        ClientPlayConnectionEvents.JOIN.register((_, _, _) -> onJoin());
        ClientPlayConnectionEvents.DISCONNECT.register((_, _) ->
        {
            onDisconnect();
            dispose();
        });
    }
}