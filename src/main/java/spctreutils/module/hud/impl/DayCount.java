package spctreutils.module.hud.impl;

import net.minecraft.world.clock.ClockManager;
import net.minecraft.world.timeline.Timelines;
import spctreutils.module.hud.HudElement;

public class DayCount extends HudElement
{
    public DayCount()
    {
        super("Day Count", "Day", "Displays the number of days the world has existed.");
    }

    @Override
    protected void onTick()
    {
        ClockManager clockManager = mc.level.clockManager();
        mc.level.registryAccess().get(Timelines.OVERWORLD_DAY).ifPresent((timeline) ->
        {
            long dayCount = (timeline.value().getTotalTicks(clockManager) / 24000L) + 1;
            setText(String.valueOf(dayCount));
        });
    }
}
