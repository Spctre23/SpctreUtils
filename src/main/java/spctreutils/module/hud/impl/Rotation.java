package spctreutils.module.hud.impl;

import net.minecraft.util.Mth;
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
        float yaw = Mth.wrapDegrees(mc.gameRenderer.mainCamera().yRot());
        float pitch = Mth.wrapDegrees(mc.gameRenderer.mainCamera().xRot());

        String rotationText = String.format("§f%.1f §7/ §f%.1f", yaw, pitch);
        setText(rotationText);
    }
}
