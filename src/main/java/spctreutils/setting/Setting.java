package spctreutils.setting;

import spctreutils.config.ConfigManager;

public class Setting<T>
{
    public final String name;
    public final String description;
    private final T defaultValue;
    private final Class<T> type;
    private String key;

    public Setting(String name, String description, T defaultValue, Class<T> type)
    {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.type = type;
    }

    public Setting(String name, T defaultValue, Class<T> type)
    {
        this.name = name;
        this.description = "";
        this.defaultValue = defaultValue;
        this.type = type;
    }

    public void setKey(String featureName)
    {
        this.key = featureName + "." + name;
    }

    @SuppressWarnings("unchecked")
    public T getValue()
    {
        Object raw = ConfigManager.config.settings.get(key);
        if (raw == null) return defaultValue;
        if (type == Float.class && raw instanceof Double d) return type.cast(d.floatValue());
        return type.isInstance(raw) ? type.cast(raw) : defaultValue;
    }

    public void setValue(T value)
    {
        ConfigManager.config.settings.put(key, value);
        ConfigManager.save();
    }

    public T getDefault() { return defaultValue; }

    public Class<T> getType() { return type; }

    public String getName() { return name; }

    public String getDescription() { return description; }
}