package spctreutils.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import spctreutils.SpctreUtils;

import java.util.Collection;

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
                .options(SpctreUtils.instance.feature.getOptions())
                .options(SpctreUtils.instance.feature.getExtraOptions())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("HUD"))
                .options(SpctreUtils.instance.hud.getExtraOptions())
                .options(SpctreUtils.instance.hud.getOptions())
                .groups(SpctreUtils.instance.hud.getGroups())
                .build())
            .build()
            .generateScreen(parent);
    }
}