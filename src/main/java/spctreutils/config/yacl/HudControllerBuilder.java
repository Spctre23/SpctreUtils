package spctreutils.config.yacl;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;

public class HudControllerBuilder
{
    public static ControllerBuilder<String> create(Option<String> option)
    {
        return () -> new HudController(option);
    }
}
