package io.github.xfacthd.pnj.impl.encoder.preprocessor;

import io.github.xfacthd.pnj.api.define.ColorFormat;
import io.github.xfacthd.pnj.impl.encoder.data.EncodingImage;
import io.github.xfacthd.pnj.impl.util.Util;

public final class TransparencyExtractor
{
    public static void process(EncodingImage image)
    {
        if (image.getColorFormat().isAlphaUsed())
        {
            processElementAlpha(image);
            return;
        }

        int alphaCount = getPaletteAlphaCount(image);
        if (alphaCount > 0)
        {
            processPaletteAlpha(image, alphaCount);
        }
    }

    private static int getPaletteAlphaCount(EncodingImage image)
    {
        if (image.getColorFormat() != ColorFormat.PALETTE) return -1;

        byte[] palette = image.getPalette();
        for (int i = 0; i < palette.length; i += 4)
        {
            if (palette[i + 3] == (byte) 0xFF)
            {
                return i / 4;
            }
        }
        return 0;
    }

    private static void processPaletteAlpha(EncodingImage image, int alphaCount)
    {
        byte[] palette = image.getPalette();
        byte[] alpha = new byte[alphaCount];
        for (int i = 0; i < alphaCount; i++)
        {
            alpha[i] = palette[i * 4 + 3];
        }
        image.setTransparency(alpha);
    }

    private static void processElementAlpha(EncodingImage image)
    {
        int size = image.getWidth() * image.getHeight();
        int depth = image.getSampleDepth();
        ColorFormat format = image.getColorFormat();
        int bytesPerPixel = format.getBytePerPixel(depth / 8, false);
        byte[] pixels = image.getPixels();
        int maxAlpha = (1 << depth) - 1;
        long markerColor = -1L;
        for (int i = 0; i < size; i++)
        {
            long color = pack(pixels, i * bytesPerPixel, bytesPerPixel);
            long masked = color & maxAlpha;
            if (masked == maxAlpha)
            {
                // Fully opaque => ignore
                continue;
            }
            else if (masked != 0)
            {
                // Not fully transparent => can't extract transparency marker
                return;
            }

            if (markerColor == -1)
            {
                markerColor = color;
            }
            else if (color != markerColor)
            {
                // Multiple fully transparent colors => can't extract single alpha marker
                return;
            }
        }

        if (markerColor != -1)
        {
            // Strip off the alpha component
            markerColor >>>= depth;

            int elems = format.getElementCount() - 1;
            int bytes = elems * 2;
            byte[] alphaMarker = new byte[bytes];
            if (depth == 16)
            {
                for (int i = 0; i < bytes; i++)
                {
                    byte val = (byte) ((markerColor >> (bytes - 1 - i)) & 0x000000FF);
                    alphaMarker[i] = val;
                }
            }
            else
            {
                for (int i = 0; i < elems; i++)
                {
                    byte val = (byte) ((markerColor >> (elems - 1 - i)) & 0x000000FF);
                    alphaMarker[i * 2 + 1] = val;
                }
            }
            image.setTransparency(alphaMarker);
        }
    }

    private static long pack(byte[] pixels, int offset, int byteCount)
    {
        long value = 0;
        for (int i = 0; i < byteCount; i++)
        {
            int idx = offset + i;
            value |= (long) Util.uint8_t(pixels[idx]) << ((byteCount - 1 - i) * 8);
        }
        return value;
    }



    private TransparencyExtractor() { }
}
