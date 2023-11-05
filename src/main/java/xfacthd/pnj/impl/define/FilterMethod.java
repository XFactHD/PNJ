package xfacthd.pnj.impl.define;

public enum FilterMethod
{
    ADAPTIVE,
    ;

    private static final FilterMethod[] VALUES = values();
    private static final int COUNT = VALUES.length;

    public static FilterMethod decode(byte typeCode)
    {
        if (typeCode >= 0 && typeCode < COUNT)
        {
            return VALUES[typeCode];
        }
        throw new IllegalArgumentException("Invalid filter method: " + typeCode);
    }
}
