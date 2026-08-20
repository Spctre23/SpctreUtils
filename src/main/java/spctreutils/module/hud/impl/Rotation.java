package spctreutils.module.hud.impl;

import spctreutils.module.hud.HudElement;

import java.util.List;

public class Rotation extends HudElement
{
    public Rotation()
    {
        super("Rotation", "Facing", "Displays your pitch and yaw.", List.of());
    }

    @Override
    protected void onTick()
    {
        float yaw = mc.gameRenderer.mainCamera().rotation().x * 360;
        float pitch = mc.gameRenderer.mainCamera().rotation().y * 360;

        String rotationText = String.format("§f%.1f §7/ §f%.1f", yaw, pitch);
        setText(rotationText);
    }
}
