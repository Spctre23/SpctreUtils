package spctreutils.screen;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class TextFieldScreen extends Screen
{
    private final Consumer<String> txtConsumer;

    public TextFieldScreen(Consumer<String> textConsumer)
    {
        super(Component.literal("Search nearby metadata"));
        this.txtConsumer = textConsumer;
    }

    @Override
    protected void init()
    {
        int w = width / 3;
        int h = font.lineHeight * 2;
        int x = (width / 2) - (w / 2);
        int y = (height / 2) - (h / 2);

        EditBox editBox = new EditBox(
            font,
            x, y, w, h,
            Component.literal("Search"));

        editBox.setMaxLength(128);
        editBox.setResponder(txtConsumer);

        addRenderableWidget(editBox);
        setInitialFocus(editBox);
    }

    @Override
    public boolean keyPressed(final KeyEvent event)
    {
        if (event.isConfirmation())
            onClose();
        else super.keyPressed(event);
        return true;
    }
}
