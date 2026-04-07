package spctreutils.hud;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import spctreutils.util.ColorHelper;

import java.awt.*;
import java.util.function.Supplier;

public abstract class HudElement
{
    protected final Minecraft mc;
    private boolean enabled;
    private Component content = null;
    private int prefixColor;
    private final String prefix;

    protected HudElement(String prefix, Color prefixColor, Supplier<Boolean> configGetter)
    {
        this.prefix = prefix + ": ";
        this.mc = Minecraft.getInstance();
        this.prefixColor = ColorHelper.argbToHex(prefixColor);
        this.enabled = configGetter.get();
        initialize();
    }

    protected HudElement(String prefix, Supplier<Boolean> configGetter)
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
    }

    private void initialize()
    {
        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            if (enabled && client.player != null) onTick();
        });
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
