package spctreutils.helper;

import net.minecraft.client.Minecraft;

import java.util.Arrays;

public class TickHelper
{
    public static double getTPS()
    {
        return Math.min(1000.0 / getMSPT(), 20.0);
    }

    public static double getMSPT()
    {
        Minecraft mc = Minecraft.getInstance();
        return Arrays.stream(mc.getSingleplayerServer().getTickTimesNanos()).average().orElse(0) / 1_000_000.0;
    }
}
