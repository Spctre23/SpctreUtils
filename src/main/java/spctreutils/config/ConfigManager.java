package spctreutils.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.world.InteractionResult;
import spctreutils.SpctreUtils;
import spctreutils.feature.impl.*;
import spctreutils.hud.impl.Durability;
import spctreutils.hud.impl.EntityOwner;

public class ConfigManager
{
    public static ModConfig config;

    public static void initialize()
    {
        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        AutoConfig.getConfigHolder(ModConfig.class).registerSaveListener((holder, config) ->
        {
            if (SpctreUtils.instance != null)
            {
                SpctreUtils.instance.feature.get(NoClip.class).syncFromConfig(config.noClip);
                SpctreUtils.instance.feature.get(CopyPos.class).syncFromConfig(config.copyPos);
                SpctreUtils.instance.feature.get(PlayerTracker.class).syncFromConfig(config.playerTracker);
                SpctreUtils.instance.feature.get(ForcePlace.class).syncFromConfig(config.forcePlace);
                SpctreUtils.instance.feature.get(Invulnerable.class).syncFromConfig(config.invulnerable);
                SpctreUtils.instance.hud.get(Durability.class).syncFromConfig(config.durability);
                SpctreUtils.instance.hud.get(EntityOwner.class).syncFromConfig(config.entityOwner);

            }
            return InteractionResult.SUCCESS;
        });
    }

    public static void save()
    {
        AutoConfig.getConfigHolder(ModConfig.class).save();
    }
}
