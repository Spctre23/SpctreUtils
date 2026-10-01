package spctreutils.module.feature.impl;

import spctreutils.module.feature.FeatureBase;
import spctreutils.setting.Setting;

import java.util.List;

public class BypassChatRestrictions extends FeatureBase
{
    public static final Setting<Boolean> NO_PROFANITY_FILTER = new Setting<>("No Profanity Filter", false, Boolean.class);

    public BypassChatRestrictions()
    {
        super("Bypass Chat Restrictions", "Bypasses chat restrictions imposed by profile settings, allowing you to chat freely.", List.of(NO_PROFANITY_FILTER)); // this needs to be tested
    }
}
