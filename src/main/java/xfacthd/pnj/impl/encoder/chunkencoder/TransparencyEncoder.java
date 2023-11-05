package xfacthd.pnj.impl.encoder.chunkencoder;

import xfacthd.pnj.impl.define.ChunkType;
import xfacthd.pnj.impl.encoder.PNJEncoderImpl;
import xfacthd.pnj.impl.encoder.data.EncodingImage;

import java.io.IOException;
import java.io.OutputStream;

public final class TransparencyEncoder
{
    public static void encode(OutputStream stream, EncodingImage image) throws IOException
    {
        byte[] data = image.getTransparency();
        if (data != null)
        {
            PNJEncoderImpl.encodeChunk(stream, ChunkType.tRNS, data, data.length);
        }
    }



    private TransparencyEncoder() { }
}
