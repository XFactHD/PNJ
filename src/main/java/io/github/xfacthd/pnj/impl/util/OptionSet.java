package io.github.xfacthd.pnj.impl.util;

public final class OptionSet<T extends Enum<T>>
{
    private final long bits;

    @SafeVarargs
    public OptionSet(T... options)
    {
        long bits = 0;
        for (T option : options)
        {
            bits |= (1L << option.ordinal());
        }
        this.bits = bits;
    }

    public boolean contains(T option)
    {
        return (bits & (1L << option.ordinal())) != 0;
    }
}
