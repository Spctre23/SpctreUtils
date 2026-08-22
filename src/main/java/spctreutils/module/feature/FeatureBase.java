package spctreutils.module.feature;

import spctreutils.config.ConfigManager;
import spctreutils.module.Module;
import spctreutils.setting.Setting;

import java.util.List;

public abstract class FeatureBase extends Module
{
    protected FeatureBase(String name, String description, List<Setting<?>> settings)
    {
        super(name, description, settings);
    }

    protected FeatureBase(String name, String description)
    {
        this(name, description, List.of());
    }

    protected FeatureBase(String name)
    {
        this(name, "");
    }

    @Override
    protected boolean getConfigValue()
    {
        return ConfigManager.config.featureStates.getOrDefault(getClass().getSimpleName(), false);
    }

    @Override
    protected void setConfigValue(boolean value)
    {
        ConfigManager.config.featureStates.put(getClass().getSimpleName(), value);
        ConfigManager.save();
    }
}