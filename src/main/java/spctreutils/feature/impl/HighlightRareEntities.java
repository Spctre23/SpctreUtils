package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.phys.AABB;
import spctreutils.feature.Feature;
import spctreutils.helper.EntityHelper;
import spctreutils.helper.RenderHelper;

import java.awt.*;

public class HighlightRareEntities extends Feature
{
    public HighlightRareEntities()
    {
        super("Highlight Rare Entities", "Highlights rare entities with an outline.");
    }

    @Override
    protected void onRender(WorldRenderContext context)
    {
        EntityHelper.forEach(entity ->
        {
            AABB aabb = null;
            Color color = null;
            if (entity instanceof Panda panda)
            {
                Panda.Gene main = panda.getMainGene(), hidden = panda.getHiddenGene();
                if ((main == Panda.Gene.BROWN && !main.isRecessive()) || (main == Panda.Gene.BROWN && hidden == Panda.Gene.BROWN))
                {
                    color = Color.YELLOW;
                    aabb = entity.getBoundingBox();
                }
            }
            else if (entity instanceof Axolotl axolotl && axolotl.getVariant() == Axolotl.Variant.BLUE)
            {
                color = Color.CYAN;
                aabb = entity.getBoundingBox();
            }
            else if (entity instanceof Goat goat && goat.isScreamingGoat())
            {
                color = Color.RED;
                aabb = entity.getBoundingBox();
            }

            if (aabb == null) return;
            RenderHelper.drawOutline(context, aabb, color, true);
        });
    }
}
