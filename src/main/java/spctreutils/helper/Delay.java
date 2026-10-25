package spctreutils.helper;

public final class Delay
{
    private long endTime = 0;

    public void set(double seconds)
    {
        endTime = System.currentTimeMillis() + (long) (seconds * 1000);
    }

    public boolean isOver()
    {
        return System.currentTimeMillis() >= endTime;
    }
}
