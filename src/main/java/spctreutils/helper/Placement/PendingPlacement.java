package spctreutils.helper.Placement;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class PendingPlacement
{
    private PendingPlacement() {}

    public static BlockState state;
    public static BlockPos placementPos;

    public static void clear()
    {
        state = null;
        placementPos = null;
    }
}