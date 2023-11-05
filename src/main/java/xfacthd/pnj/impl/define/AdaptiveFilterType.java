package xfacthd.pnj.impl.define;

import xfacthd.pnj.impl.decoder.data.DecodingImage;
import xfacthd.pnj.impl.decoder.chunkdecoder.pixel.PixelDecoder;
import xfacthd.pnj.impl.util.Util;

public enum AdaptiveFilterType
{
    NONE    ((img, dec, idx, data) -> data),
    SUB     ((img, dec, idx, data) -> data + dec.getPixelData(idx - dec.getBytesPerPixel(), false)),
    UP      ((img, dec, idx, data) -> data + dec.getPixelData(idx - dec.getScanlineSize(), true)),
    AVERAGE ((img, dec, idx, data) ->
    {
        int left = dec.getPixelData(idx - dec.getBytesPerPixel(), false);
        int up = dec.getPixelData(idx - dec.getScanlineSize(), true);
        return data + ((left + up) / 2);
    }),
    PAETH   ((img, dec, idx, data) ->
    {
        int left = dec.getPixelData(idx - dec.getBytesPerPixel(), false);
        int up = dec.getPixelData(idx - dec.getScanlineSize(), true);
        int leftUp = dec.getPixelData(idx - dec.getScanlineSize() - dec.getBytesPerPixel(), true);
        return data + Util.paethPredictor(left, up, leftUp);
    }),
    ;

    private static final AdaptiveFilterType[] TYPES = values();
    private static final int COUNT = TYPES.length;

    private final Filter filter;

    AdaptiveFilterType(Filter filter)
    {
        this.filter = filter;
    }

    public byte apply(DecodingImage image, PixelDecoder dec, int index, byte data)
    {
        int value = filter.apply(image, dec, index, Util.uint8_t(data));
        return (byte) ((value % 256) & 0x000000FF);
    }



    public static AdaptiveFilterType decode(byte typeCode)
    {
        if (typeCode >= 0 && typeCode < COUNT)
        {
            return TYPES[typeCode];
        }
        throw new IllegalArgumentException("Invalid adaptive filter type: " + typeCode);
    }



    @FunctionalInterface
    private interface Filter
    {
        int apply(DecodingImage image, PixelDecoder dec, int index, int data);
    }
}
