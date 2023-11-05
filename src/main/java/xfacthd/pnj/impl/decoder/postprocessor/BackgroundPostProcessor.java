package xfacthd.pnj.impl.decoder.postprocessor;

import xfacthd.pnj.api.define.ColorFormat;
import xfacthd.pnj.api.define.DecoderOption;
import xfacthd.pnj.impl.decoder.data.DecodingImage;
import xfacthd.pnj.impl.data.chunk.BackgroundColor;
import xfacthd.pnj.impl.data.chunk.Palette;
import xfacthd.pnj.impl.define.Constants;
import xfacthd.pnj.impl.util.OptionSet;
import xfacthd.pnj.impl.util.Util;

public final class BackgroundPostProcessor
{
    public static void process(DecodingImage image, OptionSet<DecoderOption> optionSet)
    {
        if (!optionSet.contains(DecoderOption.APPLY_BACKGROUND)) return;

        BackgroundColor background = image.getChunkData(Constants.Data.KEY_BACKGROUND_COLOR);
        if (background == null) return;

        if (!image.colorFormat().isAlphaUsed() && !image.hasChunkData(Constants.Data.KEY_TRANSPARENCY))
        {
            throw new IllegalArgumentException("Can't apply background to image without alpha");
        }

        ColorFormat format = image.colorFormat();
        if (format == ColorFormat.PALETTE)
        {
            Palette palette = image.getChunkData(Constants.Data.KEY_PALETTE);
            processBackground(palette.get(background.getPaletteIndex()), image, format);
        }
        else
        {
            processBackground(background.getElementColor(format, image.sampleDepth()), image, format);
        }
    }

    private static void processBackground(byte[] color, DecodingImage image, ColorFormat format)
    {
        byte[] pixels = image.pixels();
        int depth = image.sampleDepth();
        int bytesPerElement = Math.max(depth / 8, 1);
        int sizeNoAlpha = format.getBytePerPixel(bytesPerElement, false);
        int sizeWithAlpha = format.getBytePerPixel(bytesPerElement, true);
        if (format.isAlphaUsed())
        {
            sizeNoAlpha -= bytesPerElement;
        }
        int maxValue = (1 << depth) - 1;

        if (depth == 16)
        {
            for (int i = 0; i < pixels.length; i += sizeWithAlpha)
            {
                averageWord(pixels, i, color, sizeNoAlpha, maxValue);
            }
        }
        else
        {
            for (int i = 0; i < pixels.length; i += sizeWithAlpha)
            {
                averageByte(pixels, i, color, sizeNoAlpha, maxValue);
            }
        }
    }

    private static void averageByte(byte[] pixels, int pixelIdx, byte[] color, int sizeNoAlpha, int maxValue)
    {
        int alphaStart = pixelIdx + sizeNoAlpha;
        int alpha = Util.uint8_t(pixels[alphaStart]);
        int invAlpha = maxValue - alpha;
        for (int i = 0; i < sizeNoAlpha; i++)
        {
            int elemIdx = pixelIdx + i;
            int elemSrc = Util.uint8_t(pixels[elemIdx]);
            int elemColor = Util.uint8_t(color[i]);
            int elemOut = ((elemSrc * alpha) + (elemColor * invAlpha)) / maxValue;
            pixels[elemIdx] = (byte) (elemOut & maxValue);
        }
        pixels[alphaStart] = (byte) (maxValue & 0x000000FF);
    }

    private static void averageWord(byte[] pixels, int pixelIdx, byte[] color, int sizeNoAlpha, long maxValue)
    {
        int alphaStart = pixelIdx + sizeNoAlpha;
        long alpha = Util.uint16_t(pixels[alphaStart], pixels[alphaStart + 1]);
        long invAlpha = maxValue - alpha;
        for (int i = 0; i < sizeNoAlpha; i += 2)
        {
            int elemMsb = pixelIdx + i;
            int elemLsb = elemMsb + 1;
            long elemSrc = Util.uint16_t(pixels[elemMsb], pixels[elemLsb]);
            long elemColor = Util.uint16_t(color[i], color[i + 1]);
            long elemOut = ((elemSrc * alpha) + (elemColor * invAlpha)) / maxValue;
            pixels[elemMsb] = (byte) ((elemOut >> 8) & 0xFF);
            pixels[elemLsb] = (byte) (elemOut & 0xFF);
        }
        pixels[alphaStart] = (byte) 0xFF;
        pixels[alphaStart + 1] = (byte) 0xFF;
    }



    private BackgroundPostProcessor() { }
}
