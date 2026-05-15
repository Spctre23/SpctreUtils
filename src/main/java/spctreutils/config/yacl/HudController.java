package spctreutils.config.yacl;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.network.chat.Component;
import spctreutils.hud.HudElement;

public class HudController implements Controller<HudElement>
{
    private final Option<HudElement> option;

    public HudController(Option<HudElement> option)
    {
        this.option = option;
    }

    @Override
    public Option<HudElement> option() { return option; }

    @Override
    public Component formatValue()
    {
        return Component.literal(option.pendingValue().toString());
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension)
    {
        return new HudControllerElement(this, screen, widgetDimension);
    }
}
