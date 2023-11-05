package xfacthd.pnj.impl.define;

public enum CompressionMethod
{
    DEFLATE,
    ;

    private static final CompressionMethod[] VALUES = values();
    private static final int COUNT = VALUES.length;

    public static CompressionMethod decode(byte typeCode)
    {
        if (typeCode >= 0 && typeCode < COUNT)
        {
            return VALUES[typeCode];
        }
        throw new IllegalArgumentException("Invalid compression method: " + typeCode);
    }
}
