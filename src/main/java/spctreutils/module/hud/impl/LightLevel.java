package spctreutils.module.hud.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.LevelLightEngine;
import spctreutils.module.hud.HudElement;
import spctreutils.setting.Setting;

import java.util.List;

public class LightLevel extends HudElement
{
    private static final Setting<Boolean> skyLight = new Setting<>("Also display sky light level", false, Boolean.class);

    public LightLevel()
    {
        super("Light Level", "Displays the light level at your position.", List.of(skyLight));
    }

    @Override
    protected void onTick()
    {
        BlockPos pos = mc.player.getOnPos();
        LevelLightEngine lightEngine = mc.level.getChunkSource().getLightEngine();
        int blockLightLevel = lightEngine.getLayerListener(LightLayer.BLOCK).getLightValue(pos);

        StringBuilder sb = new StringBuilder("BL: ");
        sb.append(blockLightLevel);

        if (skyLight.getValue())
        {
            int skyLightLevel = lightEngine.getLayerListener(LightLayer.BLOCK).getLightValue(pos);
            sb.append("| SL:" + skyLightLevel);
        }

        setText(sb.toString());
    }
}
