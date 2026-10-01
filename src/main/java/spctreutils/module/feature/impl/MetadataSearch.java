package spctreutils.module.feature.impl;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.apache.commons.lang3.StringUtils;
import spctreutils.key.Keybind;
import spctreutils.module.feature.ToggleFeature;
import spctreutils.helper.World.ChunkHelper;
import spctreutils.helper.World.EntityHelper;
import spctreutils.helper.Visual.RenderHelper;
import spctreutils.screen.TextFieldScreen;
import spctreutils.setting.Setting;

import java.awt.*;
import java.util.HashSet;
import java.util.List;

public class MetadataSearch extends ToggleFeature
{
    private static final Setting<Boolean> SEARCH_SIGNS = new Setting<>("Search signs", true, Boolean.class);
    private static final Setting<Boolean> SEARCH_ITEM_FRAMES = new Setting<>("Search item frames", true, Boolean.class);
    private static final Setting<Boolean> INCLUDE_RENAMED_ITEMS = new Setting<>("Include renamed items", true, Boolean.class);
    private static final Setting<Color> SIGN_HIGHLIGHT_COLOR = new Setting<>("Sign highlight color", Color.ORANGE, Color.class);
    private static final Setting<Color> ITEM_FRAME_HIGHLIGHT_COLOR = new Setting<>("Item frame highlight color", Color.GREEN, Color.class);

    private final Keybind searchModifierBind = new Keybind("Metadata Search - Search Modifier", InputConstants.KEY_LCONTROL);
    private final Keybind searchTriggerBind = new Keybind("Metadata Search - Search Trigger", InputConstants.KEY_F);

    private final HashSet<BlockPos> matchingSignPositions = new HashSet<>();

    private String searchQuery = "";

    public MetadataSearch()
    {
        super("Metadata Search", "Highlights signs or item frames that contain a specified string.", List.of(SEARCH_SIGNS, SEARCH_ITEM_FRAMES, INCLUDE_RENAMED_ITEMS/*, SIGN_TEXT, ITEM_FRAME_TEXT*/, SIGN_HIGHLIGHT_COLOR, ITEM_FRAME_HIGHLIGHT_COLOR));

        registerSearchBind();
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
    protected void onRender()
    {
        if (SEARCH_SIGNS.getValue())
        {
            for (BlockPos pos : matchingSignPositions)
            {
                if (pos == null) continue;
                RenderHelper.drawOutline(pos, SIGN_HIGHLIGHT_COLOR.getValue());
            }
        }

        if (!SEARCH_ITEM_FRAMES.getValue() || searchQuery.isBlank()) return;
        EntityHelper.forEachOfType(ItemFrame.class, frame ->
        {
            ItemStack stack = frame.getItem();
            if (stack.isEmpty()) return;

            String target = searchQuery.toLowerCase();
            String item = StringUtils.substringAfterLast(String.valueOf(stack.getItem()), ":");
            Component name = stack.getCustomName();

            boolean matches = item.contains(target) || (INCLUDE_RENAMED_ITEMS.getValue() && name != null && name.getString().toLowerCase().contains(target));
            if (!matches) return;
            RenderHelper.drawOutline(frame.getBoundingBox(), ITEM_FRAME_HIGHLIGHT_COLOR.getValue());
        });
    }

    @Override
    protected void onEnabled() { matchingSignPositions.clear(); }

    @Override
    protected void onDisabled() { matchingSignPositions.clear(); }

    private void findMatchingSigns()
    {
        String target = searchQuery.toLowerCase();
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
            if (sign.getFrontText().getMessage(i, false).getString().toLowerCase().contains(target) && !searchQuery.isBlank())
                return true;
        }
        return false;
    }

    private void registerSearchBind()
    {
        searchTriggerBind.onPressed(() ->
        {
            if (!searchModifierBind.isDown() || mc.gui.screen() != null) return;
            searchQuery = "";
            mc.setScreenAndShow(new TextFieldScreen((text -> searchQuery = text)));
        });
    }
}
