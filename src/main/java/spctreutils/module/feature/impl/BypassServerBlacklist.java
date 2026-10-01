package spctreutils.module.feature.impl;

import spctreutils.module.feature.FeatureBase;

import java.util.List;

public class BypassServerBlacklist extends FeatureBase
{
    public BypassServerBlacklist()
    {
        super("Bypass Server Blacklist", """
                Allows you to join servers that are blacklisted by Mojang.
                USE AT YOUR OWN RISK!""",
            List.of());
    }
}
