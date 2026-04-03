package spctreutils;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spctreutils.config.ModConfig;
import spctreutils.feature.impl.CopyPos;
import spctreutils.feature.Feature;
import spctreutils.feature.impl.ForcePlace;
import spctreutils.feature.impl.NoClip;
import spctreutils.feature.impl.PlayerTracker;

import java.util.*;

public class SpctreUtils implements ClientModInitializer
{
	public static final String MOD_ID = "spctreutils";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static SpctreUtils instance;

	public static LocalPlayer localPlayer = null;
	public static Player serverPlayer = null;
	public static Map<UUID, ServerPlayer> serverPlayers = new HashMap<>();
	public static ModConfig config = new ModConfig();

	public List<Feature> features = new ArrayList<>();

	@Override
	public void onInitializeClient()
	{
		instance = this;
		features.add(new NoClip());
		features.add(new PlayerTracker());
		features.add(new CopyPos());
		features.add(new ForcePlace());

		LOGGER.info("SpctreUtils initialized.");
	}

	public <T extends Feature> T getFeature(Class<T> type)
	{
		for (Feature feature : features)
		{
			if (type.isInstance(feature))
				return type.cast(feature);
		}
		return null;
	}
}