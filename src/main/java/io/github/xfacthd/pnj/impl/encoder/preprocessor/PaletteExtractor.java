package io.github.xfacthd.pnj.impl.encoder.preprocessor;

import io.github.xfacthd.pnj.impl.encoder.data.EncodingImage;
import io.github.xfacthd.pnj.impl.util.Util;

import java.util.*;

public final class PaletteExtractor
{
    public static void process(EncodingImage image)
    {
        if (image.getSampleDepth() != 8 || !image.getColorFormat().isColorUsed()) return;

        Comparator<Integer> colorComparator = PaletteExtractor::compareColor;
        SortedSet<Integer> distinctColors = new TreeSet<>(colorComparator);
        int size = image.getWidth() * image.getHeight();
        int bytesPerPixel = image.getColorFormat().getElementCount();
        byte[] pixels = image.getPixels();
        for (int i = 0; i < size; i++)
        {
            int color = pack(pixels, i * bytesPerPixel, bytesPerPixel);
            if (distinctColors.add(color) && distinctColors.size() > 256)
            {
                // More than 256 colors => can't palettize, bail out
                return;
            }
        }

        Integer[] distinctArray = distinctColors.toArray(Integer[]::new);
        byte[] newPixels = new byte[size];
        for (int i = 0; i < size; i++)
        {
            int color = pack(pixels, i * bytesPerPixel, bytesPerPixel);
            int idx = Arrays.binarySearch(distinctArray, color, colorComparator);
            newPixels[i] = (byte) (idx & 0x000000FF);
        }

        int count = distinctColors.size();
        byte[] palette = new byte[count * 4];
        int index = 0;
        for (int value : distinctColors)
        {
            Util.intToBytes(palette, index * 4, value);
            index++;
        }

        int bitDepth = Math.max(Integer.SIZE - Integer.numberOfLeadingZeros(count - 1), 1);
        if (bitDepth > 8)
        {
            throw new IllegalStateException("Invalid palette bit depth: " + bitDepth);
        }
        else if (bitDepth > 4)
        {
            bitDepth = 8;
        }
        else if (bitDepth > 2)
        {
            bitDepth = 4;
        }
        image.setPalette(palette, newPixels, bitDepth);
    }

    private static int pack(byte[] pixels, int offset, int byteCount)
    {
        int value = 0;
        for (int i = 0; i < byteCount; i++)
        {
            int idx = offset + i;
            value |= Util.uint8_t(pixels[idx]) << ((3 - i) * 8);
        }
        if (byteCount == 3)
        {
            value |= 0x000000FF;
        }
        return value;
    }

    private static int compareColor(int colorA, int colorB)
    {
        byte alphaA = Util.uint8_t(colorA);
        byte alphaB = Util.uint8_t(colorB);
        int alphaComp = Byte.compare(alphaA, alphaB);
        if (alphaComp == 0)
        {
            return Integer.compare(colorA, colorB);
        }
        return alphaComp;
    }



    private PaletteExtractor() { }
}
