package spctreutils.feature.impl;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.phys.AABB;
import spctreutils.feature.Feature;
import spctreutils.util.RenderHelper;

public class HighlightRareEntities extends Feature
{
    public HighlightRareEntities()
    {
        super("Highlight Rare Entities", "Highlight rare entities with an outline.");

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context ->
        {
            if (!enabled) return;

            for (Entity entity : mc.level.entitiesForRendering())
            {
                if (entity == null) continue;

                AABB pos = null;
                float r = 0, g = 0, b = 0;
                if (entity instanceof Panda panda)
                {
                    Panda.Gene main = panda.getMainGene(), hidden = panda.getHiddenGene();
                    if ((main == Panda.Gene.BROWN && !main.isRecessive()) || (main == Panda.Gene.BROWN && hidden == Panda.Gene.BROWN))
                    {
                        r = g = 1f;
                        pos = entity.getBoundingBox();
                    }
                }
                else if (entity instanceof Axolotl axolotl && axolotl.getVariant() == Axolotl.Variant.BLUE)
                {
                    g = b = 1f;
                    pos = entity.getBoundingBox();
                }
                else if (entity instanceof Goat goat && goat.isScreamingGoat())
                {
                    r = 1f;
                    pos = entity.getBoundingBox();
                }

                if (pos == null) continue;
                RenderHelper.drawOutline(context, pos, r, g, b, 1f, true);
            }
        });
    }
}
