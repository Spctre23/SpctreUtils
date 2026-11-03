package spctreutils.module.feature.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Input;
import spctreutils.module.feature.FeatureManager;
import spctreutils.module.feature.ToggleFeature;

public class SecondaryPlace extends ToggleFeature
{
    public SecondaryPlace()
    {
        super("Secondary Place", "A more convenient toggle for the \"SHIFT + PLACE\" action, so you can easily place chests without holding shift.");
    }

    @Override
    protected InteractionResult onPacketSent(Packet<?> packet)
    {
        if (packet instanceof ServerboundUseItemOnPacket)
        {
            sendForceSneak();
        }
        return InteractionResult.SUCCESS;
    }

    private void sendForceSneak()
    {
        if (mc.getSingleplayerServer() != null) return;
        if (mc.player.getMainHandItem().isEmpty() && mc.player.getOffhandItem().isEmpty()) return;
        if (mc.player.input.keyPresses.shift()) return;

        Input orig = mc.player.input.keyPresses;
        mc.player.connection.send(new ServerboundPlayerInputPacket(
            new Input(orig.forward(), orig.backward(), orig.left(), orig.right(), orig.jump(), true, orig.sprint())));
    }
}
