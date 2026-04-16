package spctreutils.hud;

import com.mojang.blaze3d.platform.Window;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import spctreutils.SpctreUtils;
import spctreutils.config.ConfigManager;
import spctreutils.config.ModConfig;
import spctreutils.hud.impl.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HudManager
{
    private List<HudElement> elements = new ArrayList<>();

    public HudManager()
    {
        registerElements();
        initializeHud();
    }

    private void registerElements()
    {
        elements.add(new Position());
        elements.add(new Durability());
        elements.add(new EntityHealth());
        elements.add(new EntityOwner());
        elements.add(new GoatVariant());
        elements.add(new HorseSpeed());
        elements.add(new HorseJump());
    }

    private void initializeHud()
    {
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(SpctreUtils.MOD_ID, "hud");
        HudElementRegistry.attachElementAfter(VanillaHudElements.CHAT, resourceLocation, (guiGraphics, tickDelta) ->
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
                if (!element.isEnabled() || content == null) continue;

                int textWidth = mc.font.width(content);

                guiGraphics.drawString(
                    mc.font,
                    content,
                    width - textWidth,
                    offset -= mc.font.lineHeight,
                    element.getPrefixColor()
                );
            }
        });
    }

    public List<OptionGroup> getGroups()
    {
        return elements.stream()
            .map(HudElement::createGroup)
            .collect(Collectors.toList());
    }

    public List<Option<?>> getExtraOptions()
    {
        return List.of(
            Option.<Boolean>createBuilder()
                .name(Component.literal("HUD Enabled"))
                .binding(
                    new ModConfig().hud,
                    () -> ConfigManager.config.hud,
                    v -> { ConfigManager.config.hud = v; ConfigManager.save(); })
                .controller(TickBoxControllerBuilder::create)
                .build(),
            Option.<Color>createBuilder()
                .name(Component.literal("Prefix Color"))
                .binding(
                    new Color(new ModConfig().hudPrefixColor, true),
                    () -> new Color(ConfigManager.config.hudPrefixColor, true),
                    v -> { ConfigManager.config.hudPrefixColor = v.getRGB(); ConfigManager.save(); })
                .controller(ColorControllerBuilder::create)
                .build(),
            Option.<Color>createBuilder()
                .name(Component.literal("Text Color"))
                .binding(
                    new Color(new ModConfig().hudTextColor, true),
                    () -> new Color(ConfigManager.config.hudTextColor, true),
                    v -> { ConfigManager.config.hudTextColor = v.getRGB(); ConfigManager.save(); })
                .controller(ColorControllerBuilder::create)
                .build()
        );
    }
}
