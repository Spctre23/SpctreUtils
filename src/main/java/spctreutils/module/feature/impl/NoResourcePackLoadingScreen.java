package spctreutils.module.feature.impl;

import net.minecraft.client.gui.screens.LoadingOverlay;
import spctreutils.module.feature.Feature;

import java.util.List;

public class NoResourcePackLoadingScreen extends Feature
{
    public NoResourcePackLoadingScreen()
    {
        super("No Resource Pack Loading Screen", "Removes the annoying loading bar occupying the whole screen when you change resource packs.", List.of());
    }

    @Override
    protected void onTick()
    {
        if (mc.getOverlay() instanceof LoadingOverlay)
            mc.setOverlay(null);
    }
}
