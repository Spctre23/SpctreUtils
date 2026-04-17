package spctreutils.helper;

import java.awt.*;

public class ColorHelper
{
    public static int rgbToHex(Color color)
    {
        return color.getRGB() & 0xFFFFFF;
    }
}
