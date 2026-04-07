package spctreutils;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spctreutils.config.ConfigManager;
import spctreutils.feature.FeatureManager;
import spctreutils.hud.HudManager;

import java.util.*;

public class SpctreUtils implements ClientModInitializer
{
    public static final String MOD_ID = "spctreutils";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static SpctreUtils instance;

    public static LocalPlayer localPlayer = null;
    public static Player serverPlayer = null;
    public static Map<UUID, ServerPlayer> serverPlayers = new HashMap<>();

    public FeatureManager feature;
    public HudManager hud;

    @Override
    public void onInitializeClient()
    {
        instance = this;
        ConfigManager.initialize();
        feature = new FeatureManager();
        hud = new HudManager();

        LOGGER.info("SpctreUtils initialized.");
    }
}