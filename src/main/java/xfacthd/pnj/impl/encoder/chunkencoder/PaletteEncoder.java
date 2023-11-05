package xfacthd.pnj.impl.encoder.chunkencoder;

import xfacthd.pnj.impl.define.ChunkType;
import xfacthd.pnj.impl.encoder.PNJEncoderImpl;
import xfacthd.pnj.impl.encoder.data.EncodingImage;

import java.io.IOException;
import java.io.OutputStream;

public final class PaletteEncoder
{
    public static void encode(OutputStream stream, EncodingImage image) throws IOException
    {
        byte[] palette = image.getPalette();
        if (palette == null) return;

        int size = palette.length / 4;
        byte[] data = new byte[size * 3];
        for (int i = 0; i < size; i++)
        {
            System.arraycopy(palette, i * 4, data, i * 3, 3);
        }
        PNJEncoderImpl.encodeChunk(stream, ChunkType.PLTE, data, data.length);
    }



    private PaletteEncoder() { }
}
