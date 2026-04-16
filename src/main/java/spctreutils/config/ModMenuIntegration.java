package spctreutils.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import spctreutils.SpctreUtils;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return parent -> YetAnotherConfigLib.createBuilder()
            .title(Component.literal("SpctreUtils"))
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("Features"))
                .groups(SpctreUtils.instance.feature.getGroups())
                .options(SpctreUtils.instance.feature.getExtraOptions())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("HUD"))
                .options(SpctreUtils.instance.hud.getExtraOptions())
                .groups(SpctreUtils.instance.hud.getGroups())
                .build())
            .build()
            .generateScreen(parent);
    }
}