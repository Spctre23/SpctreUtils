package spctreutils.helper.Placement;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public final class PlacementHelper
{
    private PlacementHelper() {}

    private static final double MAX_FRACTION = 0.99;

    private static final Set<Property<?>> WHITELIST = Set.of(
        BlockStateProperties.INVERTED, BlockStateProperties.OPEN,
        BlockStateProperties.BELL_ATTACHMENT, BlockStateProperties.AXIS,
        BlockStateProperties.HALF, BlockStateProperties.ATTACH_FACE,
        BlockStateProperties.CHEST_TYPE, BlockStateProperties.MODE_COMPARATOR,
        BlockStateProperties.DOOR_HINGE, BlockStateProperties.FACING,
        BlockStateProperties.FACING_HOPPER, BlockStateProperties.HORIZONTAL_FACING,
        BlockStateProperties.ORIENTATION, BlockStateProperties.RAIL_SHAPE,
        BlockStateProperties.RAIL_SHAPE_STRAIGHT, BlockStateProperties.SLAB_TYPE,
        BlockStateProperties.STAIRS_SHAPE, BlockStateProperties.COPPER_GOLEM_POSE,
        BlockStateProperties.BITES, BlockStateProperties.DELAY,
        BlockStateProperties.NOTE, BlockStateProperties.ROTATION_16
    );

    public static boolean canPlaceHeldBlock(ItemStack stack, BlockItem blockItem, BlockHitResult hit)
    {
        if (hit.getType() != HitResult.Type.BLOCK) return false;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;

        BlockPlaceContext ctx = blockItem.updatePlacementContext(new BlockPlaceContext(mc.player, mc.player.getUsedItemHand(), stack, hit));
        if (ctx == null || !ctx.canPlace()) return false;

        BlockState state = blockItem.getBlock().getStateForPlacement(ctx);
        if (state == null) return false;

        return blockItem.canPlace(ctx, state);
    }

    public static boolean isSupported(Property<?> p)
    {
        return WHITELIST.contains(p);
    }

    public static BlockHitResult withProtocol(BlockHitResult hit, BlockState state, BlockPos placementPos)
    {
        BlockPos clicked = hit.getBlockPos();
        Vec3 loc = hit.getLocation();

        double relX = Math.min(loc.x - clicked.getX(), MAX_FRACTION);
        double x = placementPos.getX() + relX + 2 + encode(state);

        return new BlockHitResult(new Vec3(x, loc.y, loc.z), hit.getDirection(), clicked.immutable(), hit.isInside());
    }

    private static int encode(BlockState state)
    {
        int[] acc = {0, 1};

        Optional<EnumProperty<Direction>> dir = firstDirectionProperty(state);
        if (dir.isPresent() && dir.get() != BlockStateProperties.VERTICAL_DIRECTION)
        {
            acc[0] |= state.getValue(dir.get()).get3DDataValue() << 1;
            acc[1] = 4;
        }

        List<Property<?>> props = new ArrayList<>(state.getBlock().getStateDefinition().getProperties());
        props.sort(Comparator.comparing(Property::getName));

        for (Property<?> p : props)
        {
            if (dir.isPresent() && dir.get().equals(p)) continue;
            if (isSupported(p)) encodeProperty(state, p, acc);
        }
        return acc[0];
    }

    private static <T extends Comparable<T>> void encodeProperty(BlockState state, Property<T> p, int[] acc)
    {
        List<T> values = new ArrayList<>(p.getPossibleValues());
        values.sort(Comparable::compareTo);

        int index = values.indexOf(state.getValue(p));
        if (index < 0) return;

        acc[0] |= index << acc[1];
        acc[1] += Mth.log2(Mth.smallestEncompassingPowerOfTwo(values.size()));
    }

    @SuppressWarnings("unchecked")
    private static Optional<EnumProperty<Direction>> firstDirectionProperty(BlockState state)
    {
        for (Property<?> p : state.getProperties())
            if (p instanceof EnumProperty<?> e && e.getValueClass() == Direction.class)
                return Optional.of((EnumProperty<Direction>) e);
        return Optional.empty();
    }
}
