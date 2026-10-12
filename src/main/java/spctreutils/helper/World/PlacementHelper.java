package spctreutils.helper.World;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class PlacementHelper
{
    public static boolean canPlaceHeldBlock(ItemStack stack, BlockItem blockItem, BlockHitResult hit)
    {
        if (hit.getType() != HitResult.Type.BLOCK) return false;

        Minecraft mc = Minecraft.getInstance();
        BlockPlaceContext ctx = blockItem.updatePlacementContext(new BlockPlaceContext(mc.player, mc.player.getUsedItemHand(), stack, hit));
        if (ctx == null || !ctx.canPlace()) return false;

        BlockState state = blockItem.getBlock().getStateForPlacement(ctx);
        if (state == null) return false;

        return blockItem.canPlace(ctx, state);
    }
}
