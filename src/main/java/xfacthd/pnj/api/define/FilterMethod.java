package xfacthd.pnj.api.define;

/**
 * The scanline pre-filter methods supported by the PNG file format
 */
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
