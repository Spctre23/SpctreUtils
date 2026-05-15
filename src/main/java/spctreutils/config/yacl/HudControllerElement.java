package spctreutils.config.yacl;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

public class HudControllerElement extends ControllerWidget<HudController>
{
    public HudControllerElement(HudController control, YACLScreen screen, Dimension<Integer> dim)
    {
        super(control, screen, dim);
    }

    @Override
    protected void drawValueText(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
        super.drawValueText(graphics, mouseX, mouseY, delta);
    }

    @Override
    protected int getHoveredControlWidth()
    {
        return 0;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubleClick)
    {
        return super.mouseClicked(mouseButtonEvent, doubleClick);
    }
}
