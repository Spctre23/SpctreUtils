package spctreutils.mixin.feature;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spctreutils.SpctreUtils;
import spctreutils.feature.impl.NoClip;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class BlockStateBaseMixin
{
	@Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"), cancellable = true)
	private void onGetCollisionShape(BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext, CallbackInfoReturnable<VoxelShape> cir)
	{
		if (SpctreUtils.instance == null) return;
		NoClip noClip = SpctreUtils.instance.getFeature(NoClip.class);
		if (noClip == null ||
			!noClip.enabled ||
			SpctreUtils.serverPlayer == null ||
			!(collisionContext instanceof EntityCollisionContext entityCollisionContext) ||
			!(entityCollisionContext.getEntity() instanceof Player player) ||
			!player.getUUID().equals(SpctreUtils.serverPlayer.getUUID())) return;

		cir.setReturnValue(Shapes.empty());
	}
}
