package io.github.xfacthd.pnj.impl.encoder.chunkencoder;

import io.github.xfacthd.pnj.api.define.*;
import io.github.xfacthd.pnj.impl.define.ChunkType;
import io.github.xfacthd.pnj.impl.define.Constants;
import io.github.xfacthd.pnj.impl.encoder.PNJEncoderImpl;
import io.github.xfacthd.pnj.impl.encoder.data.EncodingImage;
import io.github.xfacthd.pnj.impl.util.Util;

import java.io.IOException;
import java.io.OutputStream;

public final class HeaderEncoder
{
    public static void encode(OutputStream stream, EncodingImage image) throws IOException
    {
        byte[] data = new byte[Constants.Header.LENGTH];

        int depth = image.getColorFormat() == ColorFormat.PALETTE ? image.getBitDepth() : image.getSampleDepth();

        Util.intToBytes(data, Constants.Header.OFFSET_WIDTH, image.getWidth());
        Util.intToBytes(data, Constants.Header.OFFSET_HEIGHT, image.getHeight());
        data[Constants.Header.OFFSET_BIT_DEPTH] = Util.uint8_t(depth);
        data[Constants.Header.OFFSET_COLOR_TYPE] = Util.uint8_t(image.getColorFormat().getTypeCode());
        data[Constants.Header.OFFSET_COMPRESSION_METHOD] = Util.uint8_t(CompressionMethod.DEFLATE.ordinal());
        data[Constants.Header.OFFSET_FILTER_METHOD] = Util.uint8_t(FilterMethod.ADAPTIVE.ordinal());
        data[Constants.Header.OFFSET_INTERLACE_METHOD] = Util.uint8_t(InterlaceMethod.NONE.ordinal());

        PNJEncoderImpl.encodeChunk(stream, ChunkType.IHDR, data, data.length);
    }



    private HeaderEncoder() { }
}
