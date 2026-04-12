package spctreutils.config;

import com.google.gson.*;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.world.InteractionResult;

import java.awt.*;

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
