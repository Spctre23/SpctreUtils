package spctreutils.module.hud;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import org.joml.Vector2d;
import spctreutils.helper.Msg;
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
    private ElementFlags elementFlags = new ElementFlags(AttachType.BOTTOM_RIGHT, true);
    private SubElementFlags subElementFlags = new SubElementFlags(15, 15) ;
    private int prefixColor = ConfigManager.config.hudPrefixColor;
    private int textColor = ConfigManager.config.hudTextColor;
    private final String prefix;

    private final Map<Vector2d, ItemSubElement> itemElements = new HashMap<>();
    private final Map<Vector2d, TextSubElement> textElements = new HashMap<>();

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

    public Map<Vector2d, ItemSubElement> getItemElements()
    {
        return itemElements;
    }

    public Map<Vector2d, TextSubElement> getTextElements()
    {
        return textElements;
    }

    protected void setText(String text, int color, int x, int y)
    {
        Vector2d pos = new Vector2d(x, y);
        Component contents = Component.literal(prefix).withColor(prefixColor)
            .append(Component.literal(text).withColor(color));

        if (!textElements.containsKey(pos))
        {
            textElements.put(pos, new TextSubElement(contents, (int) pos.x, (int) pos.y));
        }
        else
        {
            TextSubElement textElement = textElements.get(pos);
            textElement.text = contents;
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

    protected void setItem(Item item, int x, int y)
    {
        Vector2d pos = new Vector2d(x, y);
        if (!itemElements.containsKey(pos))
        {
            itemElements.put(pos, new ItemSubElement(item, (int) pos.x, (int) pos.y));
            Msg.sendChat("list size: " + itemElements.size());
        }
        else
        {
            ItemSubElement itemElement = itemElements.get(pos);
            itemElement.item = item;
        }
    }

    protected void clearElements()
    {
        itemElements.clear();
        textElements.clear();
    }

    protected ElementFlags getElementFlags()
    {
        return elementFlags;
    }

    protected void setElementFlags(ElementFlags elementFlags)
    {
        this.elementFlags = elementFlags;
    }

    protected void setSubElementFlags(SubElementFlags subElementFlags)
    {
        this.subElementFlags = subElementFlags;
    }

    public enum AttachType
    {
        NONE,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public record ElementFlags(AttachType attachType, boolean verticalStack) {}

    public record SubElementFlags(int xSeparator, int ySeparator) {}

    public class ItemSubElement
    {
        Item item;
        int xOffset;
        int yOffset;

        private ItemSubElement(Item item, int xOffset, int yOffset)
        {
            this.item = item;
            this.xOffset = xOffset * subElementFlags.xSeparator;
            this.yOffset = yOffset * subElementFlags.ySeparator;
        }
    }

    public class TextSubElement
    {
        Component text;
        int xOffset;
        int yOffset;

        private TextSubElement(Component text, int xOffset, int yOffset)
        {
            this.text = text;
            this.xOffset = xOffset * subElementFlags.xSeparator;
            this.yOffset = yOffset * subElementFlags.ySeparator;
        }
    }
}