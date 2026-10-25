package spctreutils.helper.Visual;

import java.awt.*;

public final class ColorHelper
{
    public static int rgbToHex(Color color)
    {
        return color.getRGB() & 0xFFFFFF;
    }
}
