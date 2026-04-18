package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.apache.commons.lang3.StringUtils;
import spctreutils.feature.Feature;
import spctreutils.helper.ChunkHelper;
import spctreutils.helper.EntityHelper;
import spctreutils.helper.RenderHelper;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.HashSet;
import java.util.List;

public class MetadataSearch extends Feature
{
    private static final Setting<Boolean> searchSigns = new Setting<>("Search signs", true, Boolean.class);
    private static final Setting<Boolean> searchItemFrames = new Setting<>("Search item frames", true, Boolean.class);
    private static final Setting<Boolean> includeRenamedItems = new Setting<>("Include renamed items", true, Boolean.class);
    private static final Setting<String> signText = new Setting<>("Sign text", "", String.class);
    private static final Setting<String> itemFrameText = new Setting<>("Item frame text", "", String.class);

    private final HashSet<BlockPos> matchingSignPositions = new HashSet<>();

    public MetadataSearch()
    {
        super("Metadata Search", "Highlights signs or item frames that contain a specified string.", List.of(searchSigns, searchItemFrames, includeRenamedItems, signText, itemFrameText));
    }

    @Override
    protected void onTick()
    {
        if (searchSigns.getValue() && mc.level.getGameTime() % 20 == 0)
        {
            matchingSignPositions.clear();
            findMatchingSigns();
        }
    }

    @Override
    protected void onRender(WorldRenderContext context)
    {
        if (searchSigns.getValue())
        {
            for (BlockPos pos : matchingSignPositions)
            {
                if (pos == null) continue;
                RenderHelper.drawOutline(context, pos, 1f, 1f, 1f, 1f, true);
            }
        }

        if (!searchItemFrames.getValue() || itemFrameText.getValue().isBlank()) return;
        EntityHelper.forEachOfType(ItemFrame.class, frame ->
        {
            ItemStack stack = frame.getItem();
            if (stack.isEmpty()) return;

            String target = itemFrameText.getValue().toLowerCase();
            String item = StringUtils.substringAfterLast(String.valueOf(stack.getItem()), ":");
            Component name = stack.getCustomName();

            boolean matches = item.contains(target) || (includeRenamedItems.getValue() && name != null && name.getString().toLowerCase().contains(target));
            if (!matches) return;
            RenderHelper.drawOutline(context, frame.getBoundingBox(), Color.WHITE, true);
        });
    }

    @Override
    protected void onEnabled() { matchingSignPositions.clear(); }

    @Override
    protected void onDisabled() { matchingSignPositions.clear(); }

    private void findMatchingSigns()
    {
        String target = signText.getValue();
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
            if (sign.getFrontText().getMessage(i, false).getString().toLowerCase().contains(target) && !signText.getValue().isBlank())
                return true;
        }
        return false;
    }
}
