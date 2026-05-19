package spctreutils.module.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.w3c.dom.Text;
import spctreutils.helper.ListHelper;
import spctreutils.module.Module;
import spctreutils.config.ConfigManager;
import spctreutils.helper.ColorHelper;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class HudElement extends Module
{
    private int prefixColor = ConfigManager.config.hudPrefixColor;
    private int textColor = ConfigManager.config.hudTextColor;
    private final String prefix;

    private final List<ItemElement> itemElements = new ArrayList<>();
    private final List<TextElement> textElements = new ArrayList<>();

    protected HudElement(String name, String prefix, String description, List<Setting<?>> settings)
    {
        super(name, description, settings);
        this.prefix = prefix.isEmpty() ? "" : prefix + ": ";
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
        clearElements();
    }

    protected int getPrefixColor()
    {
        return prefixColor;
    }

    protected int getTextColor()
    {
        return textColor;
    }

    @Nullable
    public TextElement getTextElement(int textElementIndex)
    {
        if (!ListHelper.indexExists(textElements, textElementIndex)) return null;
        return textElements.get(textElementIndex);
    }

    @Nullable
    public TextElement getTextElement()
    {
        return getTextElements().getFirst();
    }

    @Nullable
    public ItemElement getItemElement(int itemElementIndex)
    {
        if (!ListHelper.indexExists(itemElements, itemElementIndex)) return null;
        return itemElements.get(itemElementIndex);
    }

    @Nullable
    public ItemElement getItemElement()
    {
        return getItemElements().getFirst();
    }

    public List<ItemElement> getItemElements()
    {
        return itemElements;
    }

    public List<TextElement> getTextElements()
    {
        return textElements;
    }

    protected void setText(int textElementIndex, String text, int color, int x, int y)
    {
        Component contents = Component.literal(prefix).withColor(prefixColor)
            .append(Component.literal(text).withColor(color));

        if (!ListHelper.indexExists(textElements, textElementIndex))
            textElements.add(new TextElement(contents, x, y));

        textElements.get(textElementIndex).text = contents;
    }

    protected void setText(int textElementIndex, String text, Color color, int x, int y)
    {
        setText(textElementIndex, text, ColorHelper.rgbToHex(color), x, y);
    }

    protected void setText(String text, int color, int x, int y)
    {
        setText(0, text, color, x, y);
    }

    protected void setText(String text, Color color, int x, int y)
    {
        setText(text, ColorHelper.rgbToHex(color), x, y);
    }

    protected void setText(String text, int x, int y)
    {
        setText(text, textColor, x, y);
    }

    protected void setText(int textElementIndex, String text, int color)
    {
        setText(textElementIndex, text, color, 0, 0);
    }

    protected void setText(int textElementIndex, String text, Color color)
    {
        setText(textElementIndex, text, color, 0, 0);
    }

    protected void setText(String text, int color)
    {
        setText(text, color, 0, 0);
    }

    protected void setText(String text, Color color)
    {
        setText(text, color, 0, 0);
    }

    protected void setText(String text)
    {
        setText(text, 0, 0);
    }

    protected void setItem(int itemElementIndex, Item item, int x, int y)
    {
        if (!ListHelper.indexExists(itemElements, itemElementIndex))
            itemElements.add(new ItemElement(item, x, y));

        getItemElement(itemElementIndex).item = item;
    }

    protected void setItem(Item item, int x, int y)
    {
        setItem(0, item, x, y);
    }

    protected void clearElements()
    {
        itemElements.clear();
        textElements.clear();
    }

    public class ItemElement
    {
        Item item;
        int xOffset;
        int yOffset;

        private ItemElement(Item item, int xOffset, int yOffset)
        {
            this.item = item;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }

    public class TextElement
    {
        Component text;
        int xOffset;
        int yOffset;

        private TextElement(Component text, int xOffset, int yOffset)
        {
            this.text = text;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}