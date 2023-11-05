package xfacthd.pnj.impl.define;

public enum InterlaceMethod
{
    NONE,
    ADAM7,
    ;

    private static final InterlaceMethod[] VALUES = values();
    private static final int COUNT = VALUES.length;

    public static InterlaceMethod decode(byte typeCode)
    {
        if (typeCode >= 0 && typeCode < COUNT)
        {
            return VALUES[typeCode];
        }
        throw new IllegalArgumentException("Invalid interlace method: " + typeCode);
    }
}
