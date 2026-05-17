package spctreutils.module.hud;

import net.minecraft.network.chat.Component;
import spctreutils.module.Module;
import spctreutils.config.ConfigManager;
import spctreutils.helper.ColorHelper;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.List;

public abstract class HudElement extends Module
{
    protected int prefixColor = ConfigManager.config.hudPrefixColor;
    protected int textColor = ConfigManager.config.hudTextColor;
    private Component text = null;
    private final String prefix;

    protected HudElement(String name, String prefix, String description, List<Setting<?>> settings)
    {
        super(name, description, settings);
        this.prefix = prefix.equals("") ? "" : prefix + ": ";
        syncFromConfig();
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

    @Override
    protected boolean getConfigValue()
    {
        return ConfigManager.config.hudElementStates.getOrDefault(getClass().getSimpleName(), false);
    }

    @Override
    protected void setConfigValue(boolean enabled)
    {
        ConfigManager.config.hudElementStates.put(getClass().getSimpleName(), enabled);
        ConfigManager.save();
    }

    @Override
    protected void syncFromConfig()
    {
        super.syncFromConfig();

        prefixColor = ConfigManager.config.hudPrefixColor;
        textColor = ConfigManager.config.hudTextColor;
    }

    @Override
    protected void onStateChanged()
    {
        super.onStateChanged();
        dispose();
    }

    @Override
    protected void dispose()
    {
        removeText();
    }

    public int getPrefixColor()
    {
        return prefixColor;
    }

    public Component getText()
    {
        return text;
    }

    protected void setText(String text, int color)
    {
        this.text = Component.literal(prefix).withColor(prefixColor)
            .append(Component.literal(text).withColor(color));
    }

    protected void setText(String text, Color color)
    {
        setText(text, ColorHelper.rgbToHex(color));
    }

    protected void setText(String text)
    {
        setText(text, textColor);
    }

    protected void removeText()
    {
        text = null;
    }
}