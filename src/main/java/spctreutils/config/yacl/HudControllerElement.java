package spctreutils.config.yacl;

import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import spctreutils.SpctreUtils;
import spctreutils.config.ConfigManager;
import spctreutils.module.hud.HudElement;

import java.awt.*;

public class HudControllerElement extends ControllerWidget<HudController>
{
    private static final int TICKBOX_SIZE = 20;

    public HudControllerElement(HudController control, YACLScreen screen, Dimension<Integer> dim)
    {
        super(control, screen, dim);
    }

    @Override
    protected void extractValueText(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a)
    {
        String className = control.option().pendingValue();
        boolean enabled = ConfigManager.config.hudElementStates.getOrDefault(className, false);
        Minecraft mc = Minecraft.getInstance();

        Component text = control.formatValue();
        int textY = getDimension().y() + (getDimension().height()) / 2 - (mc.font.lineHeight / 2);
        graphics.text(mc.font, text, getDimension().x() + 6, textY, Color.WHITE.getRGB());

        int tickBoxX = getDimension().xLimit() - TICKBOX_SIZE;
        int tickBoxY = getDimension().y() + (getDimension().height() - TICKBOX_SIZE) / 2;

        Color tickBoxColor = enabled ? Color.GRAY : Color.DARK_GRAY;
        graphics.fill(tickBoxX, tickBoxY, tickBoxX + TICKBOX_SIZE, tickBoxY + TICKBOX_SIZE, Color.BLACK.getRGB());
        graphics.fill(tickBoxX + 1, tickBoxY + 1, tickBoxX + TICKBOX_SIZE - 1, tickBoxY + TICKBOX_SIZE - 1, tickBoxColor.getRGB());


        if (enabled)
        {
            String enabledIndicator = "✓";
            int width = (TICKBOX_SIZE / 2) - (mc.font.width(enabledIndicator) / 2);
            int height = (TICKBOX_SIZE / 2) - (mc.font.lineHeight / 2);
            graphics.text(mc.font, enabledIndicator, tickBoxX + width, tickBoxY + height, Color.WHITE.getRGB());
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick)
    {
        int tickBoxX = getDimension().xLimit() - TICKBOX_SIZE - 4;
        int tickBoxY = getDimension().y() + (getDimension().height() - TICKBOX_SIZE) / 2;

        int mx = (int) event.x();
        int my = (int) event.y();

        if (mx >= tickBoxX && mx <= tickBoxX + TICKBOX_SIZE
                && my >= tickBoxY && my <= tickBoxY + TICKBOX_SIZE)
        {
            String className = control.option().pendingValue();
            boolean current = ConfigManager.config.hudElementStates.getOrDefault(className, false);
            ConfigManager.config.hudElementStates.put(className, !current);
            ConfigManager.save();

            HudElement element = SpctreUtils.instance.hud.getElement(className);
            if (element != null) element.setEnabled(!current);

            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    protected int getHoveredControlWidth()
    {
        return TICKBOX_SIZE + 8;
    }
}
