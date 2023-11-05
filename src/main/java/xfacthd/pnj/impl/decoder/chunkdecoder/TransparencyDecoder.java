package xfacthd.pnj.impl.decoder.chunkdecoder;

import xfacthd.pnj.api.define.ColorFormat;
import xfacthd.pnj.api.define.DecoderOption;
import xfacthd.pnj.impl.data.Chunk;
import xfacthd.pnj.impl.decoder.data.DecodingImage;
import xfacthd.pnj.impl.data.chunk.Palette;
import xfacthd.pnj.impl.data.chunk.Transparency;
import xfacthd.pnj.impl.define.Constants;
import xfacthd.pnj.impl.util.OptionSet;

import java.io.IOException;

public final class TransparencyDecoder
{
    public static void decode(DecodingImage image, Chunk chunk, OptionSet<DecoderOption> optionSet) throws IOException
    {
        if (optionSet.contains(DecoderOption.IGNORE_TRANSPARENCY)) return;

        byte[] data = chunk.data();
        ColorFormat colorFormat = image.colorFormat();
        switch (colorFormat)
        {
            case GRAYSCALE, RGB ->
            {
                int expected = colorFormat.getElementCount() * 2;
                if (data.length != expected)
                {
                    throw new IOException("Transparency data for color format %s must be exactly %d bytes".formatted(
                            colorFormat, expected
                    ));
                }
            }
            case PALETTE ->
            {
                // Palette chunk is required to be before transparency by specification
                Palette palette = image.getChunkData(Constants.Data.KEY_PALETTE);
                if (data.length > palette.getSize())
                {
                    throw new IOException("Transparency data must not be larger than palette size");
                }
            }
            case GRAYSCALE_ALPHA, RGB_ALPHA -> throw new IOException(
                    "Unexpected transparency chunk for color format with full alpha channel"
            );
        }
        image.addChunkData(Constants.Data.KEY_TRANSPARENCY, new Transparency(data));
    }



    private TransparencyDecoder() { }
}
