package spctreutils.hud;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import spctreutils.config.ConfigManager;
import spctreutils.config.yacl.OptionProvider;
import spctreutils.setting.Setting;

import java.util.List;

public abstract class HudElement implements OptionProvider
{
    protected int prefixColor = ConfigManager.config.hudPrefixColor;
    protected int textColor = ConfigManager.config.hudTextColor;
    protected final Minecraft mc;
    protected final String name;
    protected final List<Setting<?>> settings;
    private boolean enabled;
    private Component text = null;
    private final String description;
    private final String prefix;

    protected HudElement(String name, String prefix, String description, List<Setting<?>> settings)
    {
        this.name = name;
        this.prefix = prefix.equals("") ? "" : prefix + ": ";
        this.description = description;
        this.mc = Minecraft.getInstance();
        this.enabled = getConfigValue();
        this.settings = settings;

        syncFromConfig();

        for (Setting<?> setting : settings)
            setting.setKey(name);

        registerEvents();
    }

    protected HudElement(String name, String prefix, String description)
    {
        this(name, prefix, description, List.of());
    }

    protected HudElement(String name, String description, List<Setting<?>> settings)
    {
        this(name, name, description, settings);
    }

    protected HudElement(String name, String description)
    {
        this(name, name, description, List.of());
    }

    protected HudElement(String name, List<Setting<?>> settings)
    {
        this(name, "", settings);
    }

    protected HudElement(String name)
    {
        this(name, "", List.of());
    }

    @Override public String getName() { return name; }

    @Override public String getDescription() { return description; }

    @Override public boolean getEnabled() { return enabled; }

    @Override public List<Setting<?>> getSettings() { return settings; }

    @Override
    public void setEnabled(boolean value)
    {
        enabled = value;
        setConfigValue(enabled);
        onStateChanged();
    }

    protected void onEnabled() {}

    protected void onDisabled() { removeText(); }

    protected void onTick() {}

    private void registerEvents()
    {
        ClientTickEvents.START_CLIENT_TICK.register(mc ->
        {
            syncFromConfig();
            if (enabled && mc.level != null && mc.player != null) onTick();
        });
    }

    private boolean getConfigValue()
    {
        return ConfigManager.config.hudElementStates.getOrDefault(getClass().getSimpleName(), false);
    }

    private void setConfigValue(boolean enabled)
    {
        ConfigManager.config.hudElementStates.put(getClass().getSimpleName(), enabled);
        ConfigManager.save();
    }

    private void syncFromConfig()
    {
        prefixColor = ConfigManager.config.hudPrefixColor;
        textColor = ConfigManager.config.hudTextColor;

        boolean newEnabled = getConfigValue();
        if (newEnabled == enabled) return;
        enabled = newEnabled;

        onStateChanged();
    }

    private void onStateChanged()
    {
        if (enabled) onEnabled();
        else onDisabled();
    }

    public int getPrefixColor() { return prefixColor; }

    public Component getText() { return text; }

    protected void setText(String text, int textColor)
    {
        this.text = Component.literal(prefix).withColor(prefixColor)
            .append(Component.literal(text).withColor(textColor));
    }

    protected void setText(String text) { setText(text, textColor); }

    protected void removeText() { text = null; }
}