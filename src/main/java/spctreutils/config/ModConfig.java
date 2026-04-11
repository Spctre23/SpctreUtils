package spctreutils.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "spctreutils")
public class ModConfig implements ConfigData
{
    public FeatureConfig feature = new FeatureConfig();
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
        public boolean getEntitiesInBlock = false;

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
        public boolean entityOwner = false;

        @ConfigEntry.Gui.Tooltip
        public boolean goatVariant = false;
    }
}