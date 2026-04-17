package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.phys.AABB;
import spctreutils.feature.Feature;
import spctreutils.helper.RenderHelper;

import java.awt.*;

public class HighlightRareEntities extends Feature
{
    public HighlightRareEntities()
    {
        super("Highlight Rare Entities", "Highlight rare entities with an outline.");
    }

    @Override
    protected void onRender(WorldRenderContext context)
    {
        for (Entity entity : mc.level.entitiesForRendering())
        {
            if (entity == null) continue;

            AABB pos = null;
            Color color = null;
            if (entity instanceof Panda panda)
            {
                Panda.Gene main = panda.getMainGene(), hidden = panda.getHiddenGene();
                if ((main == Panda.Gene.BROWN && !main.isRecessive()) || (main == Panda.Gene.BROWN && hidden == Panda.Gene.BROWN))
                {
                    color = Color.YELLOW;
                    pos = entity.getBoundingBox();
                }
            }
            else if (entity instanceof Axolotl axolotl && axolotl.getVariant() == Axolotl.Variant.BLUE)
            {
                color = Color.CYAN;
                pos = entity.getBoundingBox();
            }
            else if (entity instanceof Goat goat && goat.isScreamingGoat())
            {
                color = Color.RED;
                pos = entity.getBoundingBox();
            }

            if (pos == null) continue;
            RenderHelper.drawOutline(context, pos, color, true);
        }
    }
}
