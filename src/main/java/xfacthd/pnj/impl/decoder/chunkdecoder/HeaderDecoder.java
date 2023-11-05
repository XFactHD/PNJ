package xfacthd.pnj.impl.decoder.chunkdecoder;

import xfacthd.pnj.api.define.ColorFormat;
import xfacthd.pnj.api.define.DecoderOption;
import xfacthd.pnj.impl.data.Chunk;
import xfacthd.pnj.impl.data.ChunkList;
import xfacthd.pnj.impl.decoder.data.DecodingImage;
import xfacthd.pnj.impl.define.*;
import xfacthd.pnj.impl.util.OptionSet;
import xfacthd.pnj.impl.util.Util;

import java.io.IOException;

public final class HeaderDecoder
{
    public static DecodingImage decode(ChunkList chunks, OptionSet<DecoderOption> optionSet) throws IOException
    {
        Chunk chunk = chunks.get(0);
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

        boolean hasPalette = chunks.containsType(ChunkType.PLTE);
        boolean canUsePalette = colorFormat == ColorFormat.RGB || colorFormat == ColorFormat.RGB_ALPHA || colorFormat == ColorFormat.PALETTE;
        if ((hasPalette && !canUsePalette) || (!hasPalette && colorFormat.isPaletteUsed()))
        {
            throw new IOException(hasPalette ? "Unexpected palette" : "Missing palette");
        }

        boolean addAlpha = chunks.containsType(ChunkType.tRNS) && !optionSet.contains(DecoderOption.IGNORE_TRANSPARENCY);
        if (colorFormat.isAlphaUsed() && addAlpha)
        {
            throw new IOException("Color formats with alpha channel don't support additional transparency chunk");
        }

        return DecodingImage.create(width, height, bitDepth, colorFormat, compression, filter, interlace, addAlpha);
    }



    private HeaderDecoder() { }
}
