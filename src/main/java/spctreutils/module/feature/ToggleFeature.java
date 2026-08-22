package spctreutils.module.feature;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.world.InteractionResult;
import spctreutils.component.TextComp;
import spctreutils.event.MouseEvent;
import spctreutils.helper.Visual.Msg;
import spctreutils.key.Keybind;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.List;

public abstract class ToggleFeature extends FeatureBase
{
    protected Keybind keybind;

    protected ToggleFeature(String name, String description, int keyCode, List<Setting<?>> settings)
    {
        super(name, description, settings);
        keybind = new Keybind(name, keyCode);

        registerKeybind();
    }

    protected ToggleFeature(String name, String description, List<Setting<?>> settings)
    {
        this(name, description, InputConstants.UNKNOWN.getValue(), settings);
    }

    protected ToggleFeature(String name, String description)
    {
        this(name, description, List.of());
    }

    protected ToggleFeature(String name)
    {
        this(name, "");
    }

    protected InteractionResult onMouseScrolled(double delta)
    {
        return InteractionResult.PASS;
    }

    protected void onRender(LevelRenderContext context) {}

    protected void toggle()
    {
        setState(!enabled);
    }

    @Override
    protected void onStateChanged()
    {
        sendToggleNotification();
    }

    @Override
    protected void registerEvents()
    {
        super.registerEvents();
        LevelRenderEvents.END_MAIN.register(context ->
        {
            if (enabled && mc.level != null && mc.player != null) onRender(context);
        });
        MouseEvent.SCROLL.register(this::onMouseScrolled);
    }

    protected void registerKeybind()
    {
        keybind.onPressed(this::toggle);
    }

    private void sendToggleNotification()
    {
        Color color = enabled ? Color.GREEN : Color.RED;
        String text = enabled ? name + " = ON" : name + " = OFF";
        Msg.sendHud(new TextComp(text, color));
    }
}