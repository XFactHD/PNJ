package xfacthd.pnj.impl.decoder.data;

import xfacthd.pnj.api.data.Image;
import xfacthd.pnj.api.data.PngHeader;
import xfacthd.pnj.api.define.*;
import xfacthd.pnj.impl.define.*;
import xfacthd.pnj.impl.util.FormatConverter;

import java.util.IdentityHashMap;
import java.util.Map;

public record DecodingImage(
        int width,
        int height,
        int bitDepth,
        int sampleDepth,
        ColorFormat colorFormat,
        CompressionMethod compression,
        FilterMethod filter,
        InterlaceMethod interlace,
        Map<DataKey<?>, Object> chunkData,
        byte[] pixels
)
{
    public <T> void addChunkData(DataKey<T> key, T data)
    {
        chunkData.put(key, data);
    }

    public boolean hasChunkData(DataKey<?> key)
    {
        return chunkData.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getChunkData(DataKey<T> key)
    {
        return (T) chunkData.get(key);
    }

    public Image finish()
    {
        boolean addAlpha = chunkData.containsKey(Constants.Data.KEY_TRANSPARENCY);
        ColorFormat format = switch (colorFormat)
        {
            case RGB, PALETTE -> addAlpha ? ColorFormat.RGB_ALPHA : ColorFormat.RGB;
            case GRAYSCALE -> addAlpha ? ColorFormat.GRAYSCALE_ALPHA : ColorFormat.GRAYSCALE;
            default -> colorFormat;
        };
        int outDepth = sampleDepth;
        if (addAlpha && outDepth < 8)
        {
            // Grayscale with alpha only supports 8 and 16 bit depth, plain grayscale supports lower though
            FormatConverter.fixFormatViolationInPlace(pixels, sampleDepth);
            outDepth = 8;
        }
        return new Image(width, height, format, outDepth, pixels);
    }



    public static DecodingImage create(PngHeader header, boolean addAlpha)
    {
        int width = header.width();
        int height = header.height();
        ColorFormat format = header.colorFormat();
        int bitDepth = header.bitDepth();
        int sampleDepth = format.getSampleDepthFromBitDepth(bitDepth);
        int bytesPerElem = Math.max(sampleDepth / 8, 1);
        int bytesPerPixel = format.getBytePerPixel(bytesPerElem, addAlpha);
        byte[] pixels = new byte[width * height * bytesPerPixel];

        return new DecodingImage(
                width,
                height,
                bitDepth,
                sampleDepth,
                format,
                header.compression(),
                header.filter(),
                header.interlace(),
                new IdentityHashMap<>(),
                pixels
        );
    }
}
