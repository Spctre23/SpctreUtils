package spctreutils.helper;

import java.awt.*;

public class ColorHelper
{
    public static int rgbToHex(Color color)
    {
        return color.getRGB() & 0xFFFFFF;
    }

    public static int argbToHex(Color color)
    {
        return color.getRGB() & 0xFFFFFFFF;
    }
}
