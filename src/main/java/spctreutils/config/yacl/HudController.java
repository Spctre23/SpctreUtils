package spctreutils.config.yacl;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.network.chat.Component;
import spctreutils.SpctreUtils;
import spctreutils.module.hud.HudElement;

public record HudController(Option<String> option) implements Controller<String>
{
    @Override
    public Component formatValue()
    {
        String className = option.pendingValue();
        if (className == null) return Component.empty();

        HudElement element = SpctreUtils.instance.hud.getElement(className);
        return element != null ? Component.literal(element.getName()) : Component.literal(className);
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension)
    {
        return new HudControllerElement(this, screen, widgetDimension);
    }
}
