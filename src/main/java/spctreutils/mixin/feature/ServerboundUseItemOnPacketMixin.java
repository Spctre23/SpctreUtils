package spctreutils.mixin.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import spctreutils.helper.Placement.PendingPlacement;
import spctreutils.helper.Placement.PlacementHelper;

@Mixin(ServerboundUseItemOnPacket.class)
public class ServerboundUseItemOnPacketMixin
{
    @ModifyVariable(method = "<init>(Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;I)V", at = @At("HEAD"), argsOnly = true, name = "hitResult")
    private static BlockHitResult embedFlexiblePlacementState(BlockHitResult hitResult)
    {
        BlockState state = PendingPlacement.state;
        BlockPos placementPos = PendingPlacement.placementPos;
        if (state == null || placementPos == null) return hitResult;

        PendingPlacement.clear();
        return PlacementHelper.withProtocol(hitResult, state, placementPos);
    }
}
