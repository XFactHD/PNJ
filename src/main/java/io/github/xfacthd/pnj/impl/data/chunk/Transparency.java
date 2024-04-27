package io.github.xfacthd.pnj.impl.data.chunk;

import io.github.xfacthd.pnj.api.define.ColorFormat;

public final class Transparency
{
    private final byte[] data;

    public Transparency(byte[] data)
    {
        this.data = data;
    }

    public byte getPaletteAlpha(int paletteIdx, int depth)
    {
        if (paletteIdx < data.length)
        {
            return data[paletteIdx];
        }
        return (byte) ((1 << depth) - 1);
    }

    public byte[] getElementAlpha(ColorFormat colorFormat, int depth)
    {
        byte[] out = data;
        if (depth < 16)
        {
            int count = colorFormat.getElementCount();
            out = new byte[count];
            for (int i = 0; i < count; i++)
            {
                out[i] = data[i * 2 + 1];
            }
        }
        return out;
    }
}
