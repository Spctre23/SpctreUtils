package spctreutils.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class ModConfig
{
    public static final ConfigClassHandler<ModConfig> HANDLER = ConfigClassHandler.createBuilder(ModConfig.class)
        .id(ResourceLocation.fromNamespaceAndPath("spctreutils", "config"))
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(FabricLoader.getInstance().getConfigDir().resolve("spctreutils.json"))
            .build())
        .build();

    // Features
    @SerialEntry public boolean noClip = false;
    @SerialEntry public boolean playerTracker = false;
    @SerialEntry public boolean copyPos = false;
    @SerialEntry public boolean forcePlace = false;
    @SerialEntry public boolean invulnerable = false;
    @SerialEntry public boolean getEntitiesInBlock = false;
    @SerialEntry public float flyingSpeed = 0.05f;

    // HUD
    @SerialEntry public boolean hud = false;
    @SerialEntry public boolean durability = false;
    @SerialEntry public boolean entityHealth = false;
    @SerialEntry public boolean entityOwner = false;
    @SerialEntry public boolean goatVariant = false;
    @SerialEntry public boolean horseSpeed = false;
    @SerialEntry public boolean horseJump = false;
    @SerialEntry public boolean position = false;
    @SerialEntry public boolean positionScaled = false;
    @SerialEntry public int hudPrefixColor = Color.lightGray.getRGB();
    @SerialEntry public int hudTextColor = Color.WHITE.getRGB();
}