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
    private static final int TEXTURE_SIZE = 15;

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
            int textY = height - chatBarHeight;

            for (HudElement element : elements)
            {
                for (HudElement.ItemElement itemElement : element.getItemElements())
                {
                    if (itemElement.item == null) continue;

                    int itemX = itemElement.xOffset + ((width - TEXTURE_SIZE) / 2);
                    int itemY = itemElement.yOffset + (Math.round((height / 2.0f) - 8.0f));

                    guiGraphics.renderItem(
                        itemElement.item.getDefaultInstance(),
                        itemX,
                        itemY
                    );
                }

                for (HudElement.TextElement textElement : element.getTextElements())
                {
                    if (textElement.text == null) continue;

                    int textWidth = mc.font.width(textElement.text);
                    int textX = textElement.xOffset + (width - textWidth);

                    if (textElement.yOffset == 0)
                        textY -= mc.font.lineHeight;
                    else
                        textY = textElement.yOffset;

                    guiGraphics.drawString(
                        mc.font,
                        textElement.text,
                        textX,
                        textY,
                        element.getPrefixColor()
                    );
                }
            }
        });
    }

/*    private void initializeHud()
    {
        Minecraft mc = Minecraft.getInstance();
        Identifier identifier = Identifier.fromNamespaceAndPath(SpctreUtils.MOD_ID, "hud");
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, identifier, (guiGraphics, tickDelta) ->
        {
            if (!ConfigManager.config.hud || mc.player == null || mc.options.hideGui) return;

            Window window = mc.getWindow();
            int width = window.getGuiScaledWidth();
            int height = window.getGuiScaledHeight();
            int chatBarHeight = mc.gui.getChat().isChatFocused() ? 14 : 0;
            int offset = height - chatBarHeight;

            for (HudElement element : elements)
            {
                Component content = element.getText();
                if (!element.getEnabled() || content == null) continue;

                int textWidth = mc.font.width(content);

                int screenWidth = mc.getWindow().getGuiScaledWidth();
                int screenHeight = mc.getWindow().getGuiScaledHeight();

                int x = (screenWidth - TEXTURE_SIZE) / 2;
                int y = Math.round((screenHeight / 2.0f) - 8.0f);

                guiGraphics.drawString(
                    mc.font,
                    content,
                    width - textWidth,
                    offset -= mc.font.lineHeight,
                    element.getPrefixColor()
                );
            }
        });
    }*/

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
