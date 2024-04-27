package io.github.xfacthd.pnj.impl.define;

import io.github.xfacthd.pnj.impl.decoder.chunkdecoder.pixel.PixelDecoder;
import io.github.xfacthd.pnj.impl.util.Util;

public enum AdaptiveFilterType
{
    NONE
    {
        @Override
        int apply0(PixelDecoder dec, int index, int data)
        {
            return data;
        }
    },
    SUB
    {
        @Override
        int apply0(PixelDecoder dec, int index, int data)
        {
            return data + dec.getPreviousPixel(index);
        }
    },
    UP
    {
        @Override
        int apply0(PixelDecoder dec, int index, int data)
        {
            return data + dec.getPixelFromPreviousScanline(index);
        }
    },
    AVERAGE
    {
        @Override
        int apply0(PixelDecoder dec, int index, int data)
        {
            int left = dec.getPreviousPixel(index);
            int up = dec.getPixelFromPreviousScanline(index);
            return data + ((left + up) / 2);
        }
    },
    PAETH
    {
        @Override
        int apply0(PixelDecoder dec, int index, int data)
        {
            int left = dec.getPreviousPixel(index);
            int up = dec.getPixelFromPreviousScanline(index);
            int leftUp = dec.getPixelFromPreviousScanline(index - dec.getBytesPerPixel());
            return data + Util.paethPredictor(left, up, leftUp);
        }
    },
    ;

    private static final AdaptiveFilterType[] TYPES = values();
    private static final int COUNT = TYPES.length;

    public final byte apply(PixelDecoder dec, int index, byte data)
    {
        int value = apply0(dec, index, Util.uint8_t(data));
        return (byte) ((value % 256) & 0x000000FF);
    }

    abstract int apply0(PixelDecoder dec, int index, int data);



    public static AdaptiveFilterType decode(byte typeCode)
    {
        if (typeCode >= 0 && typeCode < COUNT)
        {
            return TYPES[typeCode];
        }
        throw new IllegalArgumentException("Invalid adaptive filter type: " + typeCode);
    }
}
