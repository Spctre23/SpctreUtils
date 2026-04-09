package spctreutils.hud;

import com.mojang.blaze3d.platform.Window;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import spctreutils.SpctreUtils;
import spctreutils.config.ConfigManager;
import spctreutils.hud.impl.Durability;
import spctreutils.hud.impl.EntityOwner;
import spctreutils.hud.impl.GoatVariant;

import java.util.ArrayList;
import java.util.List;

public class HudManager
{
    public List<HudElement> elements = new ArrayList<>();

    public HudManager()
    {
        registerElements();
        initializeHud();
    }

    public void registerElements()
    {
        elements.add(new Durability());
        elements.add(new EntityOwner());
        elements.add(new GoatVariant());
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
                Component content = element.getContent();
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
}
