package spctreutils.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
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
                .options(SpctreUtils.instance.feature.getOptions())
                .groups(SpctreUtils.instance.feature.getGroups())
                .options(SpctreUtils.instance.feature.getExtraOptions())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("HUD"))
                .group(SpctreUtils.instance.hud.getExtraOptions())
                .options(SpctreUtils.instance.hud.getOptions())
                .groups(SpctreUtils.instance.hud.getGroups())
                .build())
            .build()
            .generateScreen(parent);
    }
}