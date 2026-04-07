package spctreutils.feature;

import spctreutils.feature.impl.*;

import java.util.ArrayList;
import java.util.List;

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
    }

    public <T extends Feature> T get(Class<T> type)
    {
        for (Feature feature : features)
        {
            if (type.isInstance(feature))
                return type.cast(feature);
        }
        return null;
    }
}
