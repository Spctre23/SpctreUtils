package spctreutils.module.hud;

import com.mojang.blaze3d.platform.Window;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import spctreutils.SpctreUtils;
import spctreutils.config.ConfigManager;
import spctreutils.config.ModConfig;
import spctreutils.config.yacl.HudControllerBuilder;
import spctreutils.module.hud.impl.*;
import spctreutils.module.hud.impl.Dimension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HudManager
{
    private List<HudElement> elements = new ArrayList<>();
    private List<HudElement> elementsDefaultOrder = new ArrayList<>();

    public HudManager()
    {
        registerElements();
        reorderElements();
        registerHud();
    }

    private void registerElements()
    {
        elements.add(new Biome());
        elements.add(new Dimension());
        elements.add(new Position());
        elements.add(new HorseSpeed());
        elements.add(new HorseJump());
        elements.add(new GoatVariant());
        elements.add(new Acceleration());
        elements.add(new Durability());
        elements.add(new Speed());
        elements.add(new Rotation());
        elements.add(new EntityHealth());
        elements.add(new EntityOwner());
        elements.add(new LightLevel());
        elements.add(new PlayerCount());
        elements.add(new TPS());
        elements.add(new FPS());
        elements.add(new Ping());
        elements.add(new Armor());

        elementsDefaultOrder = elements;
    }

    @Nullable
    public HudElement getElement(String className)
    {
        return elements.stream()
            .filter(e -> e.getClass().getSimpleName().equals(className))
            .findFirst()
            .orElse(null);
    }

    private void registerHud()
    {
        Minecraft mc = Minecraft.getInstance();
        Identifier identifier = Identifier.fromNamespaceAndPath(SpctreUtils.MOD_ID, "hud");

        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, identifier, (guiGraphics, _) ->
        {
            if (!ConfigManager.config.hud) return;

            final Window window = mc.getWindow();
            final int width = window.getGuiScaledWidth();
            final int height = window.getGuiScaledHeight();
            final int chatBarHeight = mc.gui.hud.getChat().isChatFocused() ? 14 : 0;
            final double guiScale = window.getGuiScale();
            final float scale = snapScale(ConfigManager.config.hudScale, guiScale);

            float topStackOffset = 0;
            float bottomStackOffset = 0;

            for (HudElement element : elements)
            {
                HudElement.Layout layout = element.getLayout();
                final boolean isTop = (layout.attachTo() == HudElement.AttachTo.TOP_LEFT || layout.attachTo() == HudElement.AttachTo.TOP_RIGHT);
                final boolean isRight = (layout.attachTo() == HudElement.AttachTo.TOP_RIGHT || layout.attachTo() == HudElement.AttachTo.BOTTOM_RIGHT);

                final int spacing = Math.max(1, layout.partSpacingX());
                final float columnWidth = spacing * scale;
                final int maxCol = maxColumn(element, spacing);

                final int itemCount = element.getItemParts().size();
                final int textCount = element.getTextParts().size();

                final boolean centered = isRight && itemCount > 0 && textCount > 0;

                final float itemSize = 16 * scale;
                final float itemSpacingX = layout.partSpacingX() * scale;

                for (HudElement.ItemPart itemPart : element.getItemParts().values())
                {
                    if (itemPart.item == null) continue;

                    float itemX = itemPart.xOffset * scale;
                    float itemY = itemPart.yOffset * scale;

                    if (!isTop)
                        itemY += height - chatBarHeight - itemSize;

                    if (centered)
                    {
                        int col = Math.round(itemPart.xOffset / (float) spacing);
                        itemX = columnCenterX(col, maxCol, columnWidth, width) - itemSize / 2f;
                    }
                    else if (isRight)
                    {
                        itemX += width - ((itemSize * itemCount) + ((itemSpacingX - itemSize) * (itemCount - 1)));
                    }

                    guiGraphics.pose().pushMatrix();
                    guiGraphics.pose().translate(snap(itemX, guiScale), snap(itemY, guiScale));
                    guiGraphics.pose().scale(scale, scale);
                    guiGraphics.item(itemPart.item.getDefaultInstance(), 0, 0);
                    guiGraphics.pose().popMatrix();
                }

                final float lineHeight = mc.font.lineHeight * scale;
                final float textSpacingX = layout.partSpacingX() * scale;
                final float topBuffer = 3 * scale;

                for (HudElement.TextPart textPart : element.getTextParts().values())
                {
                    if (textPart.text == null) continue;

                    float textX = textPart.xOffset * scale;
                    float textY = textPart.yOffset * scale;
                    final float textWidth = mc.font.width(textPart.text) * scale;

                    if (isTop)
                    {
                        textY += topBuffer + topStackOffset;
                        if (layout.verticalStack())
                            topStackOffset += lineHeight;
                    }
                    else
                    {
                        textY += bottomStackOffset + height - chatBarHeight - lineHeight;
                        if (layout.verticalStack())
                            bottomStackOffset -= lineHeight;
                    }

                    if (centered)
                    {
                        int col = Math.round(textPart.xOffset / (float) spacing);
                        textX = columnCenterX(col, maxCol, columnWidth, width) - textWidth / 2f;
                    }
                    else if (isRight)
                    {
                        textX += width - ((textWidth * textCount) + ((textSpacingX - textWidth) * (textCount - 1)));
                    }

                    guiGraphics.pose().pushMatrix();
                    guiGraphics.pose().translate(snap(textX, guiScale), snap(textY, guiScale));
                    guiGraphics.pose().scale(scale, scale);
                    guiGraphics.text(mc.font, textPart.text, 0, 0, element.getPrefixColor(), true);
                    guiGraphics.pose().popMatrix();
                }
            }
        });
    }

    private static int maxColumn(HudElement element, int spacing)
    {
        int max = 0;
        for (HudElement.ItemPart p : element.getItemParts().values())
            max = Math.max(max, Math.round(p.xOffset / (float) spacing));
        for (HudElement.TextPart p : element.getTextParts().values())
            max = Math.max(max, Math.round(p.xOffset / (float) spacing));
        return max;
    }

    private static float columnCenterX(int column, int maxColumn, float columnWidth, int screenWidth)
    {
        float right = screenWidth - (maxColumn - column) * columnWidth;
        return right - columnWidth / 2f;
    }

    private static float snap(float value, double guiScale)
    {
        return (float) (Math.round(value * guiScale) / guiScale);
    }

    private static float snapScale(float desired, double guiScale)
    {
        long physicalPixels = Math.max(1, Math.round(desired * guiScale));
        return (float) (physicalPixels / guiScale);
    }

    public List<OptionGroup> getGroups()
    {
        return elements.stream()
            .map(HudElement::createGroup)
            .collect(Collectors.toList());
    }

    public OptionGroup getGlobalOptions()
    {
        return OptionGroup.createBuilder()
            .name(Component.literal("Global HUD Settings"))
            .option(Option.<Boolean>createBuilder()
                .name(Component.literal("Render HUD"))
                .binding(
                    new ModConfig().hud,
                    () -> ConfigManager.config.hud, v ->
                    {
                        ConfigManager.config.hud = v;
                        ConfigManager.save();
                    })
                .controller(TickBoxControllerBuilder::create)
                .build())
            .option(Option.<Float>createBuilder()
                .name(Component.literal("HUD Scale"))
                .binding(
                    new ModConfig().hudScale,
                    () -> ConfigManager.config.hudScale, v ->
                    {
                        ConfigManager.config.hudScale = v;
                        ConfigManager.save();
                    })
                .controller(FloatFieldControllerBuilder::create)
                .build())
            .build();
    }

    public ListOption<String> getDrawOrderOption()
    {
        return ListOption.<String>createBuilder()
            .name(Component.literal("HUD Draw Order"))
            .binding(
                getDefaultOrder(),
                () -> ConfigManager.config.hudElementOrder,
                v ->
                {
                    ConfigManager.config.hudElementOrder = new ArrayList<>(v);
                    reorderElements();
                    ConfigManager.save();
                })
            .controller(HudControllerBuilder::create)
            .minimumNumberOfEntries(elementsDefaultOrder.size())
            .maximumNumberOfEntries(elementsDefaultOrder.size())
            .initial(() -> null)
            .build();
    }

    private void reorderElements()
    {
        if (ConfigManager.config.hudElementOrder.isEmpty())
        {
            ConfigManager.config.hudElementOrder = getDefaultOrder();
            ConfigManager.save();
        }

        Map<String, HudElement> byName = elements.stream()
            .collect(Collectors.toMap(e -> e.getClass().getSimpleName(), e -> e));

        List<HudElement> reordered = new ArrayList<>();
        for (String name : ConfigManager.config.hudElementOrder)
        {
            HudElement element = byName.get(name);
            if (element != null) reordered.add(element);
        }

        for (HudElement element : elements)
            if (!reordered.contains(element)) reordered.add(element);

        elements = reordered;
    }

    private List<String> getDefaultOrder()
    {
        return elementsDefaultOrder.stream()
            .map(e -> e.getClass().getSimpleName())
            .collect(Collectors.toList());
    }
}
