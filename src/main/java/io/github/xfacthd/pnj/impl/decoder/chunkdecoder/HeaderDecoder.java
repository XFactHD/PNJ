package io.github.xfacthd.pnj.impl.decoder.chunkdecoder;

import io.github.xfacthd.pnj.api.data.PngHeader;
import io.github.xfacthd.pnj.api.define.*;
import io.github.xfacthd.pnj.impl.data.Chunk;
import io.github.xfacthd.pnj.impl.data.ChunkList;
import io.github.xfacthd.pnj.impl.decoder.data.DecodingImage;
import io.github.xfacthd.pnj.impl.define.ChunkType;
import io.github.xfacthd.pnj.impl.define.Constants;
import io.github.xfacthd.pnj.impl.util.OptionSet;
import io.github.xfacthd.pnj.impl.util.Util;

import java.io.IOException;

public final class HeaderDecoder
{
    public static PngHeader decodeHeaderOnly(Chunk chunk) throws IOException
    {
        if (chunk.type() != ChunkType.IHDR)
        {
            throw new IOException("First chunk is not a header chunk");
        }

        byte[] data = chunk.data();
        if (data.length != Constants.Header.LENGTH)
        {
            throw new IOException("Unexpected header chunk size");
        }

        int width = Util.intFromBytes(data, Constants.Header.OFFSET_WIDTH);
        int height = Util.intFromBytes(data, Constants.Header.OFFSET_HEIGHT);
        int bitDepth = data[Constants.Header.OFFSET_BIT_DEPTH];
        ColorFormat colorFormat = ColorFormat.decode(data[Constants.Header.OFFSET_COLOR_TYPE]);
        CompressionMethod compression = CompressionMethod.decode(data[Constants.Header.OFFSET_COMPRESSION_METHOD]);
        FilterMethod filter = FilterMethod.decode(data[Constants.Header.OFFSET_FILTER_METHOD]);
        InterlaceMethod interlace = InterlaceMethod.decode(data[Constants.Header.OFFSET_INTERLACE_METHOD]);

        if (!colorFormat.isValidBitDepth(bitDepth))
        {
            throw new IOException("Invalid bit depth %d for color format %s".formatted(bitDepth, colorFormat));
        }

        return new PngHeader(width, height, colorFormat, bitDepth, compression, filter, interlace);
    }

    public static DecodingImage decode(ChunkList chunks, OptionSet<DecoderOption> optionSet) throws IOException
    {
        Chunk chunk = chunks.get(0);
        PngHeader header = decodeHeaderOnly(chunk);

        ColorFormat format = header.colorFormat();
        boolean hasPalette = chunks.containsType(ChunkType.PLTE);
        boolean canUsePalette = format == ColorFormat.RGB || format == ColorFormat.RGB_ALPHA || format == ColorFormat.PALETTE;
        if ((hasPalette && !canUsePalette) || (!hasPalette && format.isPaletteUsed()))
        {
            throw new IOException(hasPalette ? "Unexpected palette" : "Missing palette");
        }

        boolean addAlpha = chunks.containsType(ChunkType.tRNS) && !optionSet.contains(DecoderOption.IGNORE_TRANSPARENCY);
        if (format.isAlphaUsed() && addAlpha)
        {
            throw new IOException("Color formats with alpha channel don't support additional transparency chunk");
        }

        return DecodingImage.create(header, addAlpha);
    }



    private HeaderDecoder() { }
}
