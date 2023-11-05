package xfacthd.pnj.impl.decoder.chunkdecoder;

import xfacthd.pnj.api.define.ColorFormat;
import xfacthd.pnj.api.define.DecoderOption;
import xfacthd.pnj.impl.data.Chunk;
import xfacthd.pnj.impl.decoder.data.DecodingImage;
import xfacthd.pnj.impl.data.chunk.BackgroundColor;
import xfacthd.pnj.impl.define.Constants;
import xfacthd.pnj.impl.util.OptionSet;

import java.io.IOException;

public final class BackgroundDecoder
{
    public static void decode(DecodingImage image, Chunk chunk, OptionSet<DecoderOption> optionSet) throws IOException
    {
        if (!optionSet.contains(DecoderOption.APPLY_BACKGROUND)) return;

        byte[] data = chunk.data();
        ColorFormat colorFormat = image.colorFormat();
        if (colorFormat == ColorFormat.PALETTE)
        {
            if (data.length != 1)
            {
                throw new IOException("Transparency data for paletted images must have exactly one palette index entry");
            }
        }
        else
        {
            int elems = colorFormat.getElementCount();
            if (colorFormat.isAlphaUsed())
            {
                elems -= 1;
            }
            int expected = elems * 2;
            if (data.length != expected)
            {
                throw new IOException("Transparency data for color format %s must be exactly %d bytes".formatted(
                        colorFormat, expected
                ));
            }
        }
        image.addChunkData(Constants.Data.KEY_BACKGROUND_COLOR, new BackgroundColor(data));
    }



    private BackgroundDecoder() { }
}
