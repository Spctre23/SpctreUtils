package spctreutils.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathHelper
{
    public static double round(double value, int decimalPlaces)
    {
        BigDecimal bd = BigDecimal.valueOf(value);
        return bd.setScale(decimalPlaces, RoundingMode.HALF_UP).doubleValue();
    }
}
