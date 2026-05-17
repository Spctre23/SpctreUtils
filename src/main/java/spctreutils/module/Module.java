package spctreutils.module;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import spctreutils.config.yacl.OptionProvider;
import spctreutils.setting.Setting;

import java.util.List;

public abstract class Module implements OptionProvider
{
    protected final Minecraft mc;
    protected final String name;
    protected final List<Setting<?>> settings;
    protected boolean enabled;
    private final String description;

    protected Module(String name, String description, List<Setting<?>> settings)
    {
        this.name = name;
        this.description = description;
        this.settings = settings;
        this.mc = Minecraft.getInstance();
        this.enabled = getConfigValue();

        for (Setting<?> setting : settings)
            setting.setKey(name);

        registerEvents();
    }

    protected Module(String name, String description)
    {
        this(name, description, List.of());
    }

    protected Module(String name)
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

    protected void onTick() {}

    protected void onJoin() {}

    protected void onDisconnect() {}

    protected void dispose() {}

    protected void setState(boolean state)
    {
        enabled = state;
        setConfigValue(enabled);
        onStateChanged();
    }

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

    protected void registerEvents()
    {
        ClientTickEvents.START_CLIENT_TICK.register(mc ->
        {
            syncFromConfig();
            if (enabled && mc.level != null && mc.player != null) onTick();
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> onJoin());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
        {
            onDisconnect();
            dispose();
        });
    }
}