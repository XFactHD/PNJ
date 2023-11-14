package xfacthd.pnj.impl.decoder.chunkdecoder.pixel;

import xfacthd.pnj.api.define.ColorFormat;
import xfacthd.pnj.impl.decoder.data.DecodingImage;
import xfacthd.pnj.impl.define.AdaptiveFilterType;
import xfacthd.pnj.impl.util.Util;

import java.io.IOException;

public abstract sealed class PixelDecoder permits NoInterlacePixelDecoder, Adam7PixelDecoder
{
    protected final byte[] pixels;
    protected final int width;
    protected final int height;
    protected final int bytesPerPixelRaw;
    protected final int bytesPerPixelOut;
    protected final int bytesPerLineRaw;
    protected final int bytesPerLineOut;
    protected final int scanlineBitDepth;
    protected final boolean subByte;
    protected final int pixelsPerByte;
    protected final int pixelMask;
    protected final byte[] scanlineBuffer;
    protected int scanlineSize = 0;
    protected boolean scanlineComplete = true;
    protected AdaptiveFilterType filter = null;

    protected PixelDecoder(DecodingImage image, int scanlineBitDepth, int bytesPerPixelRaw, int bytesPerLineRaw)
    {
        this.pixels = image.pixels();
        this.width = image.width();
        this.height = image.height();
        this.bytesPerPixelRaw = bytesPerPixelRaw;
        this.bytesPerLineRaw = bytesPerLineRaw;
        this.bytesPerLineOut = image.pixels().length / height;
        this.bytesPerPixelOut = bytesPerLineOut / width;
        this.scanlineBitDepth = scanlineBitDepth;
        this.subByte = scanlineBitDepth < 8;
        this.pixelsPerByte = Math.max(8 / scanlineBitDepth, 1);
        this.pixelMask = ~(0xFF << scanlineBitDepth);
        this.scanlineBuffer = new byte[bytesPerLineRaw * 2];
    }

    public final void decode(byte[] data, int count) throws IOException
    {
        if (subByte)
        {
            for (int i = 0; i < count; i++)
            {
                if (scanlineComplete)
                {
                    filter = AdaptiveFilterType.decode(data[i]);
                    scanlineComplete = false;
                    continue;
                }

                decodeSubByteDepth(data[i]);
            }
        }
        else
        {
            for (int i = 0; i < count; i++)
            {
                if (scanlineComplete)
                {
                    filter = AdaptiveFilterType.decode(data[i]);
                    scanlineComplete = false;
                    continue;
                }

                decodeByteOrWordDepth(data[i]);
            }
        }
    }

    protected abstract void decodeByteOrWordDepth(byte data) throws IOException;

    protected abstract void decodeSubByteDepth(byte data) throws IOException;

    public final int getBytesPerPixel()
    {
        return bytesPerPixelRaw;
    }

    protected final void storePixelData(int idx, byte data)
    {
        scanlineBuffer[idx + scanlineSize] = data;
    }

    public final int getPreviousPixel(int idx)
    {
        idx -= bytesPerPixelRaw;
        if (idx >= 0)
        {
            return Util.uint8_t(scanlineBuffer[idx + scanlineSize]);
        }
        return 0;
    }

    public final int getPixelFromPreviousScanline(int idx)
    {
        if (idx >= 0)
        {
            return Util.uint8_t(scanlineBuffer[idx]);
        }
        return 0;
    }

    protected final void advanceScanlineBuffer()
    {
        System.arraycopy(scanlineBuffer, scanlineSize, scanlineBuffer, 0, scanlineSize);
    }



    public static PixelDecoder from(DecodingImage image)
    {
        int width = image.width();
        ColorFormat colorFormat = image.colorFormat();
        boolean palette = colorFormat.isPaletteUsed();
        int bytesPerElem = Math.max(image.sampleDepth() / 8, 1);
        int bytesPerPixelRaw = palette ? 1 : colorFormat.getBytePerPixel(bytesPerElem, colorFormat.isAlphaUsed());
        int depth = palette ? image.bitDepth() : image.sampleDepth();
        int bytesPerLineRaw = Util.getBytesPerLine(width, depth, palette ? 1 : colorFormat.getElementCount());

        return switch (image.interlace())
        {
            case NONE -> new NoInterlacePixelDecoder(image, depth, bytesPerPixelRaw, bytesPerLineRaw);
            case ADAM7 -> new Adam7PixelDecoder(image, depth, bytesPerPixelRaw, bytesPerLineRaw);
        };
    }
}
