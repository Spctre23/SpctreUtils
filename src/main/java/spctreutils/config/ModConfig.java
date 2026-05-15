package spctreutils.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.util.*;

public class ModConfig
{
    public static final ConfigClassHandler<ModConfig> HANDLER = ConfigClassHandler.createBuilder(ModConfig.class)
        .id(Identifier.fromNamespaceAndPath("spctreutils", "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(FabricLoader.getInstance().getConfigDir().resolve("spctreutils.json"))
            .build())
        .build();

    @SerialEntry public Map<String, Boolean> featureStates = new HashMap<>();
    @SerialEntry public float flyingSpeed = 0.05f;

    @SerialEntry public Map<String, Boolean> hudElementStates = new HashMap<>();
    @SerialEntry public boolean hud = false;
    @SerialEntry public int hudPrefixColor = Color.lightGray.getRGB();
    @SerialEntry public int hudTextColor = Color.WHITE.getRGB();

    @SerialEntry public Map<String, Object> settings = new HashMap<>();
}