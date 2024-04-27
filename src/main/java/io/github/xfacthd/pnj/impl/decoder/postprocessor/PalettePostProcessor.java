package io.github.xfacthd.pnj.impl.decoder.postprocessor;

import io.github.xfacthd.pnj.api.define.ColorFormat;
import io.github.xfacthd.pnj.impl.data.chunk.Palette;
import io.github.xfacthd.pnj.impl.decoder.data.DecodingImage;
import io.github.xfacthd.pnj.impl.define.Constants;
import io.github.xfacthd.pnj.impl.util.Util;

public final class PalettePostProcessor
{
    public static void process(DecodingImage image)
    {
        if (image.colorFormat() == ColorFormat.PALETTE)
        {
            Palette palette = image.getChunkData(Constants.Data.KEY_PALETTE);
            boolean alpha = image.hasChunkData(Constants.Data.KEY_TRANSPARENCY);
            int elemWidth = alpha ? 4 : 3;
            byte[] data = image.pixels();
            for (int i = 0; i < data.length; i += elemWidth)
            {
                int key = Util.uint8_t(data[i]);
                byte[] color = palette.get(key);
                System.arraycopy(color, 0, data, i, elemWidth);
            }
        }
    }



    private PalettePostProcessor() { }
}
