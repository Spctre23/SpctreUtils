package spctreutils.module.hud.impl;

import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import spctreutils.module.hud.HudElement;

import java.util.List;

public class Facing extends HudElement
{
    public Facing()
    {
        super("Facing", "Displays your pitch and yaw.", List.of());
    }

    @Override
    protected void onTick()
    {
        Camera cam = mc.gameRenderer.mainCamera();
        float yaw = Mth.wrapDegrees(cam.yRot());
        float pitch = Mth.wrapDegrees(cam.xRot());

        Entity camEntity = cam.entity();
        if (camEntity == null) return;
        char axis = camEntity.getDirection().getName().toUpperCase().charAt(0);

        String facingText = String.format("§f%s. %.1f §7/ §f%.1f", axis, yaw, pitch);
        setText(facingText);
    }
}
