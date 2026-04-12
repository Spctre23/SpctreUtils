package spctreutils.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.awt.*;

@Config(name = "spctreutils")
public class ModConfig implements ConfigData
{
    @ConfigEntry.Gui.CollapsibleObject
    public FeatureConfig feature = new FeatureConfig();

    @ConfigEntry.Gui.CollapsibleObject
    public HudConfig hud = new HudConfig();

    public class FeatureConfig
    {
        @ConfigEntry.Gui.Tooltip
        public boolean noClip = false;

        @ConfigEntry.Gui.Tooltip
        public boolean playerTracker = false;

        @ConfigEntry.Gui.Tooltip
        public boolean copyPos = false;

        @ConfigEntry.Gui.Tooltip
        public boolean forcePlace = false;

        @ConfigEntry.Gui.Tooltip
        public boolean invulnerable = false;

        @ConfigEntry.Gui.Tooltip
        public boolean getEntityCountAtBlock = false;

        @ConfigEntry.Gui.Tooltip
        public float flyingSpeed = 0.05F;
    }

    public class HudConfig
    {
        @ConfigEntry.Gui.Tooltip
        public boolean hud = false;

        @ConfigEntry.Gui.Tooltip
        public boolean durability = false;

        @ConfigEntry.Gui.Tooltip
        public boolean entityHealth = false;

        @ConfigEntry.Gui.Tooltip
        public boolean entityOwner = false;

        @ConfigEntry.Gui.Tooltip
        public boolean goatVariant = false;

        @ConfigEntry.Gui.Tooltip
        public boolean horseSpeed = false;

        @ConfigEntry.Gui.Tooltip
        public boolean horseJump = false;

        @ConfigEntry.Gui.Tooltip
        public boolean position = false;

        @ConfigEntry.Gui.Tooltip
        public boolean positionScaled = false;
    }
}