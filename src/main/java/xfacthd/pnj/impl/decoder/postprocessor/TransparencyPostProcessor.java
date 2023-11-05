package xfacthd.pnj.impl.decoder.postprocessor;

import xfacthd.pnj.api.define.ColorFormat;
import xfacthd.pnj.api.define.DecoderOption;
import xfacthd.pnj.impl.decoder.data.DecodingImage;
import xfacthd.pnj.impl.data.chunk.Palette;
import xfacthd.pnj.impl.data.chunk.Transparency;
import xfacthd.pnj.impl.define.Constants;
import xfacthd.pnj.impl.util.OptionSet;
import xfacthd.pnj.impl.util.Util;

import java.util.Arrays;

public final class TransparencyPostProcessor
{
    private static final byte[] ALPHA_ZERO = new byte[2];

    public static void process(DecodingImage image, OptionSet<DecoderOption> optionSet)
    {
        if (optionSet.contains(DecoderOption.IGNORE_TRANSPARENCY)) return;

        Transparency transparency = image.getChunkData(Constants.Data.KEY_TRANSPARENCY);
        if (transparency == null) return;

        ColorFormat colorFormat = image.colorFormat();
        if (colorFormat.isAlphaUsed())
        {
            throw new IllegalArgumentException("Encountered unexpected transparency chunk for format " + colorFormat);
        }

        switch (colorFormat)
        {
            case PALETTE -> processPaletteAlpha(image, transparency, image.sampleDepth());
            case GRAYSCALE, RGB -> processElementAlpha(image, transparency, colorFormat);
        }
    }

    private static void processPaletteAlpha(DecodingImage image, Transparency transparency, int depth)
    {
        Palette palette = image.getChunkData(Constants.Data.KEY_PALETTE);
        palette.attachAlpha(transparency, depth);
    }

    private static void processElementAlpha(DecodingImage image, Transparency transparency, ColorFormat colorFormat)
    {
        byte[] alphaIndicator = transparency.getElementAlpha(colorFormat, image.sampleDepth());

        byte[] pixels = image.pixels();
        int depth = image.sampleDepth();
        int bytesPerElement = Math.max(depth / 8, 1);
        int sizeRaw = colorFormat.getBytePerPixel(bytesPerElement, false);
        int sizeProcessed = colorFormat.getBytePerPixel(bytesPerElement, true);
        byte[] alphaFull = Util.getFullAlphaForDepth(depth);

        for (int i = 0; i < pixels.length; i += sizeProcessed)
        {
            int edge = i + sizeRaw;
            boolean matches = Arrays.equals(alphaIndicator, 0, sizeRaw, pixels, i, edge);
            byte[] alpha = matches ? ALPHA_ZERO : alphaFull;
            System.arraycopy(alpha, 0, pixels, edge, bytesPerElement);
        }
    }



    private TransparencyPostProcessor() { }
}
