package spctreutils.module.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.apache.commons.lang3.StringUtils;
import spctreutils.module.feature.ToggleFeature;
import spctreutils.helper.World.ChunkHelper;
import spctreutils.helper.World.EntityHelper;
import spctreutils.helper.Visual.RenderHelper;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.HashSet;
import java.util.List;

public class MetadataSearch extends ToggleFeature
{
    private static final Setting<Boolean> SEARCH_SIGNS = new Setting<>("Search signs", true, Boolean.class);
    private static final Setting<Boolean> SEARCH_ITEM_FRAMES = new Setting<>("Search item frames", true, Boolean.class);
    private static final Setting<Boolean> INCLUDE_RENAMED_ITEMS = new Setting<>("Include renamed items", true, Boolean.class);
    private static final Setting<String> SIGN_TEXT = new Setting<>("Sign text", "", String.class);
    private static final Setting<String> ITEM_FRAME_TEXT = new Setting<>("Item frame text", "", String.class);

    private final HashSet<BlockPos> matchingSignPositions = new HashSet<>();

    public MetadataSearch()
    {
        super("Metadata Search", "Highlights signs or item frames that contain a specified string.", List.of(SEARCH_SIGNS, SEARCH_ITEM_FRAMES, INCLUDE_RENAMED_ITEMS, SIGN_TEXT, ITEM_FRAME_TEXT));
    }

    @Override
    protected void onTick()
    {
        if (SEARCH_SIGNS.getValue() && mc.level.getGameTime() % 20 == 0)
        {
            matchingSignPositions.clear();
            findMatchingSigns();
        }
    }

    @Override
    protected void onRender(LevelRenderContext context)
    {
        if (SEARCH_SIGNS.getValue())
        {
            for (BlockPos pos : matchingSignPositions)
            {
                if (pos == null) continue;
                RenderHelper.drawOutline(context, pos, 1f, 1f, 1f, 1f);
            }
        }

        if (!SEARCH_ITEM_FRAMES.getValue() || ITEM_FRAME_TEXT.getValue().isBlank()) return;
        EntityHelper.forEachOfType(ItemFrame.class, frame ->
        {
            ItemStack stack = frame.getItem();
            if (stack.isEmpty()) return;

            String target = ITEM_FRAME_TEXT.getValue().toLowerCase();
            String item = StringUtils.substringAfterLast(String.valueOf(stack.getItem()), ":");
            Component name = stack.getCustomName();

            boolean matches = item.contains(target) || (INCLUDE_RENAMED_ITEMS.getValue() && name != null && name.getString().toLowerCase().contains(target));
            if (!matches) return;
            RenderHelper.drawOutline(context, frame.getBoundingBox(), Color.WHITE);
        });
    }

    @Override
    protected void onEnabled() { matchingSignPositions.clear(); }

    @Override
    protected void onDisabled() { matchingSignPositions.clear(); }

    private void findMatchingSigns()
    {
        String target = SIGN_TEXT.getValue();
        if (target.isBlank()) return;

        ChunkHelper.forEach((chunk ->
            chunk.getBlockEntities().values().stream()
                .filter(SignBlockEntity.class::isInstance)
                .map(SignBlockEntity.class::cast)
                .filter(sign -> hasMatchingLine(sign, target))
                .map(SignBlockEntity::getBlockPos)
                .forEach(matchingSignPositions::add)));
    }

    private boolean hasMatchingLine(SignBlockEntity sign, String target)
    {
        for (int i = 0; i < 4; i++)
        {
            if (sign.getFrontText().getMessage(i, false).getString().toLowerCase().contains(target) && !SIGN_TEXT.getValue().isBlank())
                return true;
        }
        return false;
    }
}
