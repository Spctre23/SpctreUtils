package spctreutils.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.world.InteractionResult;

public class ConfigManager
{
    public static ModConfig config;

    public static void initialize()
    {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        AutoConfig.getConfigHolder(ModConfig.class).registerSaveListener((holder, newConfig) ->
        {
            config = newConfig;
            return InteractionResult.SUCCESS;
        });
    }

    public static void save()
    {
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }
}
