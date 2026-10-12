package spctreutils.module.hud.impl;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import spctreutils.module.hud.HudElement;

public class Biome extends HudElement
{
    public Biome()
    {
        super("Biome", "Displays the id of the biome you are in.");
    }

    @Override
    protected void onTick()
    {
        net.minecraft.world.level.biome.Biome biome = mc.level.getBiome(mc.player.blockPosition()).value();
        Identifier id = mc.level.registryAccess().lookupOrThrow(Registries.BIOME).getKey(biome);
        setText(id.getPath());
    }
}
