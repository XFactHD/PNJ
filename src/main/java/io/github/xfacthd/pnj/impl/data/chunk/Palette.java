package io.github.xfacthd.pnj.impl.data.chunk;

public final class Palette
{
    private final byte[][] colors;
    private final int size;

    public Palette(byte[][] colors, int size)
    {
        this.colors = colors;
        this.size = size;
    }

    public byte[] get(int idx)
    {
        if (idx >= 0 && idx < size)
        {
            return colors[idx];
        }
        throw new IndexOutOfBoundsException("Invalid palette index: " + idx);
    }

    public void attachAlpha(Transparency transparency, int depth)
    {
        for (int i = 0; i < size; i++)
        {
            colors[i][3] = transparency.getPaletteAlpha(i, depth);
        }
    }

    public int getSize()
    {
        return size;
    }
}
