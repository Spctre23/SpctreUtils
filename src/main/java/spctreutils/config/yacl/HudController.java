package spctreutils.config.yacl;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.network.chat.Component;
import spctreutils.hud.HudElement;

public record HudController(Option<HudElement> option) implements Controller<HudElement>
{
    @Override
    public Component formatValue()
    {
        HudElement element = option.pendingValue();
        return element != null ? Component.literal(element.getName()) : Component.empty();
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension)
    {
        return new HudControllerElement(this, screen, widgetDimension);
    }
}
