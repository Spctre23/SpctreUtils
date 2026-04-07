package spctreutils.hud;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import spctreutils.util.ColorHelper;

import java.awt.*;
import java.util.function.Supplier;

public abstract class HudElement
{
    public final String name;
    protected final Minecraft mc;
    protected int color;
    protected String text = "";
    protected boolean enabled;

    public HudElement(String name, Color color, Supplier<Boolean> configGetter)
    {
        this.name = name;
        this.mc = Minecraft.getInstance();
        this.color = ColorHelper.argbToHex(color);
        this.enabled = configGetter.get();
        initialize();
    }

    public void onEnable() {}

    public void onDisable() {}

    public void onTick() {}

    private void setState()
    {
        if (enabled) onEnable();
        else onDisable();
    }

    private void initialize()
    {
        ClientTickEvents.START_CLIENT_TICK.register(client ->
        {
            if (enabled && client.player != null) onTick();
        });
    }

    public void syncFromConfig(boolean newValue)
    {
        if (newValue == enabled) return;
        enabled = newValue;
        setState();
    }
}
