package spctreutils.hud;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import spctreutils.config.ConfigManager;
import spctreutils.config.ModConfig;
import spctreutils.util.ColorHelper;

import java.awt.*;
import java.util.function.Function;

public abstract class HudElement
{
    protected final Minecraft mc;
    private boolean enabled;
    private Component content = null;
    private int prefixColor;
    private final String prefix;
    private final Function<ModConfig, Boolean> configGetter;

    protected HudElement(String prefix, Color prefixColor, Function<ModConfig, Boolean> configGetter)
    {
        this.prefix = prefix + ": ";
        this.mc = Minecraft.getInstance();
        this.prefixColor = ColorHelper.argbToHex(prefixColor);
        this.configGetter = configGetter;
        this.enabled = configGetter.apply(ConfigManager.config);

        initialize();
    }

    protected HudElement(String prefix, Function<ModConfig, Boolean> configGetter)
    {
        this(prefix, Color.WHITE, configGetter);
    }

    protected void onEnabled() {}

    protected void onDisabled()
    {
        removeContent();
    }

    protected void onTick() {}

    public boolean isEnabled()
    {
        return enabled;
    }

    private void initialize()
    {
        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            syncFromConfig();
            if (enabled && client.player != null) onTick();
        });
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
    }

    public int getPrefixColor()
    {
        return prefixColor;
    }

    public Component getContent()
    {
        return content;
    }

    protected void setContent(String text, Color textColor)
    {
        content = Component.literal(prefix).withColor(prefixColor).append(Component.literal(text).withColor(ColorHelper.argbToHex(textColor)));
    }

    protected void setContent(String text)
    {
        setContent(text, Color.WHITE);
    }

    protected void removeContent()
    {
        content = null;
    }
}
