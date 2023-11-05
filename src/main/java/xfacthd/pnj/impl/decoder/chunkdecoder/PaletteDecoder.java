package xfacthd.pnj.impl.decoder.chunkdecoder;

import xfacthd.pnj.impl.data.Chunk;
import xfacthd.pnj.impl.decoder.data.DecodingImage;
import xfacthd.pnj.impl.data.chunk.Palette;
import xfacthd.pnj.impl.define.Constants;

import java.io.IOException;

public final class PaletteDecoder
{
    public static void decode(DecodingImage image, Chunk chunk) throws IOException
    {
        if (!image.colorFormat().isPaletteUsed())
        {
            // Assume consuming software supports full color, so ignore embedded palette for non-palette color formats
            return;
        }

        byte[] data = chunk.data();
        if (data.length == 0 || data.length % 3 != 0)
        {
            throw new IOException("Encountered incorrectly sized palette");
        }

        int maxSize = 1 << image.bitDepth();
        int count = 0;
        byte[][] palette = new byte[maxSize][];
        for (int i = 0; i < data.length; i += 3)
        {
            byte[] color = new byte[4]; // Prepare for potential alpha channel being attached later
            System.arraycopy(data, i, color, 0, 3);
            palette[i / 3] = color;
            count++;
        }
        image.addChunkData(Constants.Data.KEY_PALETTE, new Palette(palette, count));
    }



    private PaletteDecoder() { }
}
