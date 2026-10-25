package spctreutils.setting;

import spctreutils.config.ConfigManager;

import java.awt.Color;

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
        this(name, "", defaultValue, type);
    }

    public void setKey(String moduleName)
    {
        this.key = moduleName + "." + name;
    }

    public T getValue()
    {
        Object raw = ConfigManager.config.settings.get(key);
        if (raw == null) return defaultValue;

        Object value = deserialize(raw);
        return type.isInstance(value) ? type.cast(value) : defaultValue;
    }

    public void setValue(T value)
    {
        ConfigManager.config.settings.put(key, serialize(value));
        ConfigManager.save();
    }

    private Object serialize(T value)
    {
        return value instanceof Color color ? color.getRGB() : value;
    }

    private Object deserialize(Object raw)
    {
        if (raw instanceof Number number)
        {
            if (type == Integer.class) return number.intValue();
            if (type == Long.class) return number.longValue();
            if (type == Float.class) return number.floatValue();
            if (type == Double.class) return number.doubleValue();
            if (type == Color.class) return new Color(number.intValue(), true);
        }
        return raw;
    }

    public T getDefault() { return defaultValue; }

    public Class<T> getType() { return type; }

    public String getName() { return name; }

    public String getDescription() { return description; }
}