package spctreutils.feature;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import net.minecraft.network.chat.Component;
import spctreutils.config.ConfigManager;
import spctreutils.config.ModConfig;
import spctreutils.feature.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FeatureManager
{
    public List<Feature> features = new ArrayList<>();

    public FeatureManager()
    {
        features.add(new NoClip());
        features.add(new PlayerTracker());
        features.add(new CopyPos());
        features.add(new ForcePlace());
        features.add(new Invulnerable());
        features.add(new GetEntityCount());
        features.add(new GetDistanceToAimedBlock());
        features.add(new HighlightRareEntities());
        features.add(new MetadataSearch());
    }

    public <T extends Feature> T getFeature(Class<T> type)
    {
        for (Feature feature : features)
        {
            if (type.isInstance(feature))
                return type.cast(feature);
        }
        return null;
    }

    public List<Option<?>> getOptions()
    {
        return features.stream()
            .filter(feature -> feature.getSettings().isEmpty())
            .map(Feature::createOption)
            .collect(Collectors.toList());
    }

    public List<OptionGroup> getGroups()
    {
        return features.stream()
            .filter(feature -> !feature.getSettings().isEmpty())
            .map(Feature::createGroup)
            .collect(Collectors.toList());
    }

    public List<Option<?>> getExtraOptions()
    {
        return List.of(Option.<Float>createBuilder()
            .name(Component.literal("Flying Speed"))
            .description(OptionDescription.of(Component.literal("Lets you configure creative mode flight speed.")))
            .binding(new ModConfig().flyingSpeed, () -> ConfigManager.config.flyingSpeed, v ->
            {
                ConfigManager.config.flyingSpeed = v;
                ConfigManager.save();
            })
            .controller(opt -> FloatFieldControllerBuilder.create(opt)
                .range(0.01f, 1f))
            .build()
        );
    }
}
