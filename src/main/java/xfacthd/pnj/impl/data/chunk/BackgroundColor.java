package xfacthd.pnj.impl.data.chunk;

import xfacthd.pnj.api.define.ColorFormat;
import xfacthd.pnj.impl.util.Util;

public final class BackgroundColor
{
    private final byte[] data;

    public BackgroundColor(byte[] data)
    {
        this.data = data;
    }

    public int getPaletteIndex()
    {
        if (data.length != 1)
        {
            throw new IllegalArgumentException("Malformed background chunk for paletted image");
        }
        return Util.uint8_t(data[0]);
    }

    public byte[] getElementColor(ColorFormat format, int depth)
    {
        int elems = format.getElementCount();
        if (format.isAlphaUsed())
        {
            elems -= 1;
        }

        byte[] out = data;
        if (depth < 16)
        {
            out = new byte[elems];
            for (int i = 0; i < elems; i++)
            {
                out[i] = data[i * 2 + 1];
            }
        }
        return out;
    }
}
