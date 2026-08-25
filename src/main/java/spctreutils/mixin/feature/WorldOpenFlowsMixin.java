package spctreutils.mixin.feature;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.impl.NoExperimentalWarning;

@Mixin(WorldOpenFlows.class)
public abstract class WorldOpenFlowsMixin
{
    @Shadow protected abstract void openWorldLoadBundledResourcePack(LevelStorageSource.LevelStorageAccess worldAccess, WorldStem worldStem, PackRepository packRepository, Runnable onCancel);

    @Inject(at = @At("HEAD"), method = "openWorldCheckWorldStemCompatibility", cancellable = true)
    private void skipExperimentalWarning(LevelStorageSource.LevelStorageAccess worldAccess, WorldStem worldStem, PackRepository packRepository, Runnable onCancel, CallbackInfo ci)
    {
        if (FeatureManager.isEnabled(NoExperimentalWarning.class))
        {
            LevelDataAndDimensions.WorldDataAndGenSettings worldDataGenSettings = worldStem.worldDataAndGenSettings();
            WorldData worldData = worldDataGenSettings.data();
            boolean isOldCustomized = worldDataGenSettings.genSettings().options().isOldCustomizedWorld();
            boolean isExperimental = worldData.worldGenSettingsLifecycle() != Lifecycle.stable();
            if (isOldCustomized || isExperimental)
            {
                this.openWorldLoadBundledResourcePack(worldAccess, worldStem, packRepository, onCancel);
                ci.cancel();
            }
        }
    }
}