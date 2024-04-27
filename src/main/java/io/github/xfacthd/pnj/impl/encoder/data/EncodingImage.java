package io.github.xfacthd.pnj.impl.encoder.data;

import io.github.xfacthd.pnj.api.data.Image;
import io.github.xfacthd.pnj.api.define.ColorFormat;

public final class EncodingImage
{
    private final int width;
    private final int height;
    private final int sampleDepth;
    private int bitDepth;
    private ColorFormat colorFormat;
    private byte[] pixels;
    private byte[] palette = null;
    private byte[] transparency = null;

    public EncodingImage(Image image)
    {
        this.width = image.width();
        this.height = image.height();
        this.sampleDepth = image.sampleDepth();
        this.bitDepth = sampleDepth;
        this.colorFormat = image.colorFormat();
        this.pixels = image.pixels();
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getSampleDepth()
    {
        return sampleDepth;
    }

    public int getBitDepth()
    {
        return bitDepth;
    }

    public ColorFormat getColorFormat()
    {
        return colorFormat;
    }

    public byte[] getPixels()
    {
        return pixels;
    }

    public byte[] getPalette()
    {
        return palette;
    }

    public byte[] getTransparency()
    {
        return transparency;
    }

    public void setPalette(byte[] palette, byte[] pixels, int bitDepth)
    {
        this.colorFormat = ColorFormat.PALETTE;
        this.palette = palette;
        this.pixels = pixels;
        this.bitDepth = bitDepth;
    }

    public void setTransparency(byte[] transparency)
    {
        this.transparency = transparency;
        this.colorFormat = switch (colorFormat)
        {
            case GRAYSCALE_ALPHA -> ColorFormat.GRAYSCALE;
            case RGB_ALPHA -> ColorFormat.RGB;
            case PALETTE -> ColorFormat.PALETTE;
            default -> throw new IllegalStateException("Can't extract alpha fromt format without alpha");
        };
    }
}
