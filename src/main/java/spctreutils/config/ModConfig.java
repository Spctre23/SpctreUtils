package spctreutils.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import spctreutils.SpctreUtils;
import spctreutils.feature.Feature;

public class ModConfig {
    public Screen configScreen;
    public float flyingSpeed = 0.05F;

    public void initialize(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("SpctreUtils"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

        for (Feature feature : SpctreUtils.instance.features) {
            general.addEntry(entryBuilder.startBooleanToggle(Component.literal(feature.name), feature.enabled)
                    .setDefaultValue(false)
                    .setSaveConsumer(newValue -> feature.enabled = newValue)
                    .build());
        }

        general.addEntry(entryBuilder.startFloatField(Component.literal("Flying Speed"), flyingSpeed)
                .setDefaultValue(0.05F)
                .setSaveConsumer(newValue -> flyingSpeed = newValue)
                .build());

        configScreen = builder.build();
    }
}