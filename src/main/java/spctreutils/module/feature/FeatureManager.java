package spctreutils.module.feature;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import org.jspecify.annotations.Nullable;
import spctreutils.module.feature.impl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FeatureManager
{
    private static final List<Feature> features = new ArrayList<>();

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
        features.add(new FlySpeed());
        features.add(new NoBreakDelay());
        features.add(new FastUse());
        features.add(new NoResourcePackLoadingScreen());
    }

    @Nullable
    public static <T extends Feature> T getFeature(Class<T> type)
    {
        for (Feature feature : features)
        {
            if (type.isInstance(feature))
                return type.cast(feature);
        }
        return null;
    }

    public static <T extends Feature> boolean isEnabled(Class<T> type)
    {
        Feature feature = getFeature(type);
        return feature != null && feature.getEnabled();
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
}
