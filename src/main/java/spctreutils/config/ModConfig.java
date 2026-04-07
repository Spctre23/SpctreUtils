package spctreutils.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "spctreutils")
public class ModConfig implements ConfigData
{
    public boolean noClip = false;
    public boolean playerTracker = false;
    public boolean copyPos = false;
    public boolean forcePlace = false;
    public boolean invulnerable = false;
    public float flyingSpeed = 0.05F;

    @ConfigEntry.Category("hud")
    public boolean hud = false;

    @ConfigEntry.Category("hud")
    public boolean durability = false;

    @ConfigEntry.Category("hud")
    public boolean entityOwner = false;
}