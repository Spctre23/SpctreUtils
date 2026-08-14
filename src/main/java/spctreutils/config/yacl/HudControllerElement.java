package spctreutils.config.yacl;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import org.jspecify.annotations.NonNull;

public class HudControllerElement extends ControllerWidget<HudController>
{
    public HudControllerElement(HudController control, YACLScreen screen, Dimension<Integer> dim)
    {
        super(control, screen, dim);
    }

    @Override
    protected void extractValueText(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        super.extractValueText(graphics, mouseX, mouseY, a);
    }

    @Override
    protected int getHoveredControlWidth()
    {
        return 0;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent mouseButtonEvent, boolean doubleClick)
    {
        return super.mouseClicked(mouseButtonEvent, doubleClick);
    }
}
