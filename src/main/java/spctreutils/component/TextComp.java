package spctreutils.component;

import java.awt.*;

public class TextComp
{
    private String text;
    private Color color;

    public TextComp(String text, Color color)
    {
        this.text = text;
        this.color = color;
    }

    public TextComp(String text)
    {
        this(text, Color.WHITE);
    }

    public String getText()
    {
        return text;
    }

    public Color getColor()
    {
        return color;
    }
}
