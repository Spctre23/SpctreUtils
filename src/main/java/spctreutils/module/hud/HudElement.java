package spctreutils.module.hud;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.joml.Vector2d;
import spctreutils.module.Module;
import spctreutils.config.ConfigManager;
import spctreutils.helper.ColorHelper;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class HudElement extends Module
{
    private final String prefix;
    private int prefixColor = ConfigManager.config.hudPrefixColor;
    private int textColor = ConfigManager.config.hudTextColor;

    private final Map<Vector2d, ItemPart> itemParts = new HashMap<>();
    private final Map<Vector2d, TextPart> textParts = new HashMap<>();

    private Layout layout = new Layout(AttachTo.TOP_RIGHT, true, 15, 15);

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

    protected int getPrefixColor()
    {
        return prefixColor;
    }

    protected int getTextColor()
    {
        return textColor;
    }

    public Map<Vector2d, TextPart> getTextParts()
    {
        return textParts;
    }

    protected void setText(String text, int color, int x, int y)
    {
        Vector2d pos = new Vector2d(x, y);
        Component contents = Component.literal(prefix).withColor(prefixColor)
            .append(Component.literal(text).withColor(color));

        if (!textParts.containsKey(pos))
        {
            textParts.put(pos, new TextPart(contents, (int) pos.x, (int) pos.y));
        }
        else
        {
            TextPart textPart = textParts.get(pos);
            textPart.text = contents;
        }
    }

    protected void setText(String text, Color color, int x, int y)
    {
        setText(text, ColorHelper.rgbToHex(color), x, y);
    }

    protected void setText(String text, int x, int y)
    {
        setText(text, textColor, x, y);
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

    protected void removeText(int x, int y)
    {
        Vector2d pos = new Vector2d(x, y);
        textParts.remove(pos);
    }

    public Map<Vector2d, ItemPart> getItemParts()
    {
        return itemParts;
    }

    protected void setItem(Item item, int x, int y)
    {
        Vector2d pos = new Vector2d(x, y);
        if (!itemParts.containsKey(pos))
        {
            itemParts.put(pos, new ItemPart(item, (int) pos.x, (int) pos.y));
        }
        else
        {
            ItemPart itemPart = itemParts.get(pos);
            itemPart.item = item;
        }
    }

    protected void removeItem(int x, int y)
    {
        Vector2d pos = new Vector2d(x, y);
        itemParts.remove(pos);
    }

    public class ItemPart
    {
        Item item;
        int xOffset;
        int yOffset;

        private ItemPart(Item item, int xOffset, int yOffset)
        {
            this.item = item;
            this.xOffset = xOffset * layout.partSpacingX;
            this.yOffset = yOffset * layout.partSpacingY;
        }
    }

    public class TextPart
    {
        Component text;
        int xOffset;
        int yOffset;

        private TextPart(Component text, int xOffset, int yOffset)
        {
            this.text = text;
            this.xOffset = xOffset * layout.partSpacingX;
            this.yOffset = yOffset * layout.partSpacingY;
        }
    }

    protected Layout getLayout()
    {
        return layout;
    }

    protected void setLayout(Layout layout)
    {
        this.layout = layout;
    }

    public enum AttachTo
    {
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        TOP_LEFT,
        TOP_RIGHT
    }

    public record Layout(AttachTo attachTo, boolean verticalStack, int partSpacingX, int partSpacingY) {}

    protected void clearParts()
    {
        itemParts.clear();
        textParts.clear();
    }

    protected void removePart(int x, int y)
    {
        removeItem(x, y);
        removeText(x, y);
    }

    @Override
    protected void dispose()
    {
        clearParts();
    }
}