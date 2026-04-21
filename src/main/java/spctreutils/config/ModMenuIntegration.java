package spctreutils.config;

import com.mojang.blaze3d.platform.InputConstants;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import spctreutils.SpctreUtils;
import spctreutils.key.Keybind;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi
{
    public ModMenuIntegration()
    {
        Keybind openConfigMenuKey = new Keybind("Open Config Menu", InputConstants.UNKNOWN.getValue());
        openConfigMenuKey.onPressed(() ->
        {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.screen == null) return;
            mc.setScreen(getModConfigScreenFactory().create(mc.screen));
        });
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> YetAnotherConfigLib.createBuilder()
            .title(Component.literal("SpctreUtils"))
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Features"))
                .options(SpctreUtils.instance.feature.getOptions())
                .groups(SpctreUtils.instance.feature.getGroups())
                .options(SpctreUtils.instance.feature.getExtraOptions())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("HUD"))
                .groups(SpctreUtils.instance.hud.getGroups())
                .options(SpctreUtils.instance.hud.getOptions())
                .group(SpctreUtils.instance.hud.getExtraOptions())
                .build())
            .build()
            .generateScreen(parent);
    }
}