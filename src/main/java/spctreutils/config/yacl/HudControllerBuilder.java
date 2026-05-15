package spctreutils.config.yacl;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import spctreutils.hud.HudElement;

public class HudControllerBuilder
{
    public static ControllerBuilder<HudElement> create(Option<HudElement> option)
    {
        return () -> new HudController(option);
    }
}
