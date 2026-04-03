package spctreutils.utils;

import java.awt.*;

public class ColorHelper
{
    public static int toHex(Color color)
    {
        return color.getRGB() & 0xFFFFFF;
    }
}
