package spctreutils.config;

public class ConfigManager
{
    public static ModConfig config;

    public static void initialize()
    {
        ModConfig.HANDLER.load();
        config = ModConfig.HANDLER.instance();
    }

    public static void save()
    {
        ModConfig.HANDLER.save();
    }
}