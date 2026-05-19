package spctreutils.helper;

import java.util.List;

public class ListHelper
{
    public static boolean indexExists(List<?> list, int index)
    {
        return list != null && index >= 0 && index < list.size();
    }
}
