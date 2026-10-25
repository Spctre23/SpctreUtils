package spctreutils.helper.World;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;

public final class GameModeHelper
{
    private static final Minecraft mc = Minecraft.getInstance();

    public static boolean isSurvival()
    {
        return mc.player != null && mc.player.gameMode() != null && mc.player.gameMode() == GameType.SURVIVAL;
    }

    public static boolean isAdventure()
    {
        return mc.player != null && mc.player.gameMode() != null && mc.player.gameMode() == GameType.ADVENTURE;
    }

    public static boolean isCreative()
    {
        return mc.player != null && mc.player.gameMode() != null && mc.player.gameMode() == GameType.CREATIVE;
    }

    public static boolean isSpectator()
    {
        return mc.player != null && mc.player.gameMode() != null && mc.player.gameMode() == GameType.SPECTATOR;
    }

    public static boolean isAdventureOrSurvival()
    {
        return mc.player != null && mc.player.gameMode() != null && (isAdventure() || isSurvival());
    }
}
