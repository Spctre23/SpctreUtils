package spctreutils.module.hud.impl;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import spctreutils.SpctreUtils;
import spctreutils.module.hud.HudElement;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.List;

public class Watermark extends HudElement
{
    private static final Color DEFAULT_WATERMARK_COLOR = new Color(0x55FFFF);
    private static final Setting<String> WATERMARK_TEXT = new Setting<>("Watermark text", "SpctreUtils", String.class);
    private static final Setting<Boolean> SHOW_VERSION = new Setting<>("Show version number", false, Boolean.class);
    private static final Setting<Color> VERSION_COLOR = new Setting<>("Version color", Color.GREEN, Color.class);


    public Watermark()
    {
        super("Watermark", "", "Customizable watermark.", DEFAULT_WATERMARK_COLOR, List.of(VERSION_COLOR, WATERMARK_TEXT, SHOW_VERSION));
    }

    @Override
    protected void onTick()
    {
        MutableComponent text = Component.literal(WATERMARK_TEXT.getValue()).withStyle(Style.EMPTY.withColor(getTextColor()));
        if (SHOW_VERSION.getValue())
        {
            ModMetadata metadata = FabricLoader.getInstance().getModContainer(SpctreUtils.MOD_ID)
                .orElseThrow(() -> new RuntimeException("Mod not found"))
                .getMetadata();

            String version = metadata.getVersion().getFriendlyString();
            text.append(Component.literal(" " + version).withColor(VERSION_COLOR.getValue().getRGB()));
        }
        setText(text);
    }
}
