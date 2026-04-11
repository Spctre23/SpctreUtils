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
        features.add(new GetEntityCountAtBlock());
    }
}
