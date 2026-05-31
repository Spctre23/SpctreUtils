package spctreutils.module.feature;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.world.InteractionResult;
import spctreutils.component.TextComp;
import spctreutils.event.MouseEvent;
import spctreutils.module.Module;
import spctreutils.config.ConfigManager;
import spctreutils.helper.Msg;
import spctreutils.key.Keybind;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.List;

public abstract class Feature extends Module
{
    private final Keybind keybind;
    private final KEY_BEHAVIOR keyBehavior;

    protected enum KEY_BEHAVIOR
    {
        TOGGLE,
        TRIGGER
    }

    protected Feature(String name, String description, int keyCode, KEY_BEHAVIOR keyBehavior, List<Setting<?>> settings)
    {
        super(name, description, settings);
        this.keybind = new Keybind(name, keyCode);
        this.keyBehavior = keyBehavior;

        registerKeybind();
    }

    protected Feature(String name, String description, KEY_BEHAVIOR keyBehavior, List<Setting<?>> settings)
    {
        this(name, description, InputConstants.UNKNOWN.getValue(), keyBehavior, settings);
    }

    protected Feature(String name, String description, KEY_BEHAVIOR keyBehavior)
    {
        this(name, description, keyBehavior, List.of());
    }

    protected Feature(String name, String description, List<Setting<?>> settings)
    {
        this(name, description, KEY_BEHAVIOR.TOGGLE, settings);
    }

    protected Feature(String name, String description)
    {
        this(name, description, List.of());
    }

    protected Feature(String name)
    {
        this(name, "");
    }

    protected void onKeyPressed() {}

    protected void onKeyReleased() {}

    protected void onMouseScrolled(double delta) {}

    protected void onRender(WorldRenderContext context) {}

    protected void toggle()
    {
        setState(!enabled);
    }

    @Override
    protected boolean getConfigValue()
    {
        return ConfigManager.config.featureStates.getOrDefault(getClass().getSimpleName(), false);
    }

    @Override
    protected void setConfigValue(boolean value)
    {
        ConfigManager.config.featureStates.put(getClass().getSimpleName(), value);
        ConfigManager.save();
    }

    @Override
    protected void onStateChanged()
    {
        super.onStateChanged();
        if (keyBehavior == KEY_BEHAVIOR.TOGGLE) sendToggleNotification();
    }

    @Override
    protected void registerEvents()
    {
        super.registerEvents();
        WorldRenderEvents.END_MAIN.register(context ->
        {
            if (enabled && mc.level != null && mc.player != null) onRender(context);
        });
        MouseEvent.SCROLL.register(delta ->
        {
            onMouseScrolled(delta);
            return InteractionResult.PASS;
        });
    }

    private void registerKeybind()
    {
        keybind.onPressed(() ->
        {
            if (keyBehavior == KEY_BEHAVIOR.TOGGLE)
            {
                toggle();
                return;
            }
            if (enabled) onKeyPressed();
        });

        if (keyBehavior == KEY_BEHAVIOR.TRIGGER)
        {
            keybind.onReleased(() ->
            {
                if (enabled) onKeyReleased();
            });
        }
    }

    private void sendToggleNotification()
    {
        Color color = enabled ? Color.GREEN : Color.RED;
        String text = enabled ? name + " = ON" : name + " = OFF";
        Msg.sendHud(new TextComp(text, color));
    }
}