package spctreutils.module.hud;

import com.mojang.blaze3d.platform.Window;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import spctreutils.SpctreUtils;
import spctreutils.config.ConfigManager;
import spctreutils.config.ModConfig;
import spctreutils.config.yacl.HudControllerBuilder;
import spctreutils.module.hud.impl.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HudManager
{
    @SerialEntry public List<HudElement> elements = new ArrayList<>();
    public List<HudElement> elementsDefaultOrder = new ArrayList<>();

    public HudManager()
    {
        registerElements();
        initializeHud();
    }

    private void registerElements()
    {
        elements.add(new Position());
        elements.add(new HorseSpeed());
        elements.add(new HorseJump());
        elements.add(new GoatVariant());
        elements.add(new Durability());
        elements.add(new LightLevel());
        elements.add(new Acceleration());
        elements.add(new Speed());
        elements.add(new EntityHealth());
        elements.add(new EntityOwner());
        elements.add(new TPS());
        elements.add(new FPS());
        elements.add(new Ping());
        elements.add(new Armor());

        elementsDefaultOrder = elements;
    }

    private void initializeHud()
    {
        Minecraft mc = Minecraft.getInstance();
        Identifier identifier = Identifier.fromNamespaceAndPath(SpctreUtils.MOD_ID, "hud");

        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, identifier, (guiGraphics, tickDelta) ->
        {
            Window window = mc.getWindow();
            int width = window.getGuiScaledWidth();
            int height = window.getGuiScaledHeight();
            int chatBarHeight = mc.gui.getChat().isChatFocused() ? 14 : 0;
            int verticalStackOffset = height - chatBarHeight;

            for (HudElement element : elements)
            {
                HudElement.Layout layout = element.getLayout();

                for (HudElement.ItemPart itemPart : element.getItemParts().values())
                {
                    if (itemPart.item == null) continue;

                    int itemX = 0;
                    int itemSize = 16;
                    int itemY = itemPart.yOffset + (height - chatBarHeight - itemSize);

                    switch (layout.attachTo())
                    {
                        case NONE:
                            itemX = itemPart.xOffset + (width / 2);
                            itemY += Math.round((height / 2.0f) - 8.0f);
                            break;
                        case BOTTOM_LEFT:
                            itemX = itemPart.xOffset;
                            break;
                        case BOTTOM_RIGHT:
                            itemX = itemPart.xOffset + (width - itemSize);
                            break;
                    }

                    guiGraphics.renderItem(
                        itemPart.item.getDefaultInstance(),
                        itemX,
                        itemY
                    );
                }

                for (HudElement.TextPart textPart : element.getTextParts().values())
                {
                    if (textPart.text == null) continue;

                    int textX = 0;
                    int textY = textPart.yOffset + (height - chatBarHeight - mc.font.lineHeight);
                    int textWidth = mc.font.width(textPart.text);

                    switch (layout.attachTo())
                    {
                        case NONE:
                            textX = textPart.xOffset + (width / 2);
                            textY += Math.round((height / 2.0f) - 8.0f);
                            break;
                        case BOTTOM_LEFT:
                            textX = textPart.xOffset;
                            if (layout.verticalStack())
                                verticalStackOffset -= mc.font.lineHeight;
                            break;
                        case BOTTOM_RIGHT:
                            textX = textPart.xOffset + (width - textWidth);
                            if (layout.verticalStack())
                                verticalStackOffset -= mc.font.lineHeight;
                            break;
                    }

                    if (layout.verticalStack())
                        textY = verticalStackOffset;

                    guiGraphics.drawString(
                        mc.font,
                        textPart.text,
                        textX,
                        textY,
                        element.getPrefixColor()
                    );
                }
            }
        });
    }

    public List<Option<?>> getOptions()
    {
        return elements.stream()
            .filter(element -> element.getSettings().isEmpty())
            .map(HudElement::createOption)
            .collect(Collectors.toList());
    }

    public List<OptionGroup> getGroups()
    {
        return elements.stream()
            .filter(element -> !element.getSettings().isEmpty())
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
            .option(Option.<Color>createBuilder()
                .name(Component.literal("HUD Prefix Color"))
                .binding(
                    new Color(new ModConfig().hudPrefixColor, true),
                    () -> new Color(ConfigManager.config.hudPrefixColor, true), v ->
                    {
                        ConfigManager.config.hudPrefixColor = v.getRGB();
                        ConfigManager.save();
                    })
                .controller(ColorControllerBuilder::create)
                .build())
            .option(Option.<Color>createBuilder()
                .name(Component.literal("HUD Text Color"))
                .binding(
                    new Color(new ModConfig().hudTextColor, true),
                    () -> new Color(ConfigManager.config.hudTextColor, true), v ->
                    {
                        ConfigManager.config.hudTextColor = v.getRGB();
                        ConfigManager.save();
                    })
                .controller(ColorControllerBuilder::create)
                .build())
            .build();
    }

    public ListOption<HudElement> getDrawOrderOptions()
    {
        return ListOption.<HudElement>createBuilder()
            .name(Component.literal("HUD Draw Order"))
            .binding(
                new ArrayList<>(elementsDefaultOrder),
                () -> new ArrayList<>(elements),
                v -> { elements = new ArrayList<>(v); ConfigManager.save(); })
            .controller(HudControllerBuilder::create)
            .minimumNumberOfEntries(elementsDefaultOrder.size())
            .maximumNumberOfEntries(elementsDefaultOrder.size())
            .initial(() -> null)
            .build();
    }
}
