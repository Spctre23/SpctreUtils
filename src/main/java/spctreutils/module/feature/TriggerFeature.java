package spctreutils.module.feature;

import com.mojang.blaze3d.platform.InputConstants;
import spctreutils.setting.Setting;

import java.util.List;

public abstract class TriggerFeature extends ToggleFeature
{
    protected TriggerFeature(String name, String description, int keyCode, List<Setting<?>> settings)
    {
        super(name, description, keyCode, settings);
    }

    protected TriggerFeature(String name, String description, List<Setting<?>> settings)
    {
        this(name, description, InputConstants.UNKNOWN.getValue(), settings);
    }

    protected TriggerFeature(String name, String description)
    {
        this(name, description, InputConstants.UNKNOWN.getValue(), List.of());
    }

    protected void onKeyPressed() {}

    protected void onKeyReleased() {}

    @Override
    protected void registerKeybind()
    {
        keybind.onPressed(() ->
        {
            if (enabled) onKeyPressed();
        });

        keybind.onReleased(() ->
        {
            if (enabled) onKeyReleased();
        });
    }
}