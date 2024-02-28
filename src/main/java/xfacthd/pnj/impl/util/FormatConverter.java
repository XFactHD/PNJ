package xfacthd.pnj.impl.util;

import xfacthd.pnj.api.data.Image;
import xfacthd.pnj.api.define.ColorFormat;

import java.util.Arrays;

public final class FormatConverter
{
    public static Image toFormat(Image srcImage, ColorFormat colorFormat, int sampleDepth)
    {
        int srcDepth = srcImage.sampleDepth();
        boolean sameColor = srcImage.colorFormat() == colorFormat;
        boolean sameDepth = srcDepth == sampleDepth;
        if (sameColor && sameDepth)
        {
            return srcImage;
        }

        if (colorFormat == ColorFormat.PALETTE)
        {
            throw new IllegalArgumentException("Palette color format is not valid for in-memory images");
        }
        if (!colorFormat.isValidSampleDepth(sampleDepth))
        {
            throw new IllegalArgumentException("Sample depth " + sampleDepth + " is not valid for color format " + colorFormat);
        }

        byte[] data = srcImage.pixels();
        if (!sameColor)
        {
            data = convertColorFormat(srcImage, data, colorFormat);
        }
        if (!sameDepth)
        {
            data = convertSampleDepth(data, srcDepth, sampleDepth, sameColor);
        }
        return new Image(srcImage.width(), srcImage.height(), colorFormat, sampleDepth, data);
    }

    /**
     * Upscale the image data to 8bit to fix format violations for images with transparency from the tRNS chunk
     */
    public static void fixFormatViolationInPlace(byte[] data, int srcDepth)
    {
        if (srcDepth < 8)
        {
            convertSampleDepth(data, srcDepth, 8, false);
        }
    }

    private static byte[] convertColorFormat(Image srcImage, byte[] data, ColorFormat outColorFormat)
    {
        ColorFormat srcColorFormat = srcImage.colorFormat();
        boolean srcColor = srcColorFormat.isColorUsed();
        boolean outColor = outColorFormat.isColorUsed();
        boolean srcAlpha = srcColorFormat.isAlphaUsed();
        boolean outAlpha = outColorFormat.isAlphaUsed();
        int srcCount = srcColorFormat.getElementCount();
        int outCount = outColorFormat.getElementCount();

        boolean srcWord = srcImage.sampleDepth() == 16;
        int bytesPerElement = Math.max(srcImage.sampleDepth() / 8, 1);
        int srcBytesPerPixel = srcCount * bytesPerElement;
        int outBytesPerPixel = outCount * bytesPerElement;
        byte[] out = new byte[srcImage.width() * srcImage.height() * outBytesPerPixel];

        if (srcColor == outColor) // "Fast" path for alpha-only
        {
            if (srcAlpha) // Drop alpha channel
            {
                for (int i = 0; i < data.length; i += srcBytesPerPixel)
                {
                    int outIdx = i * outCount / srcCount;
                    System.arraycopy(data, i, data, outIdx, outBytesPerPixel);
                }
            }
            else // Append fully opaque alpha channel
            {
                int alphaOffset = bytesPerElement * (outCount - 1);
                for (int i = 0; i < out.length; i += outBytesPerPixel)
                {
                    int srcIdx = i * srcCount / outCount;
                    System.arraycopy(data, srcIdx, out, i, srcBytesPerPixel);
                    int alphaIdx = i + alphaOffset;
                    Arrays.fill(out, alphaIdx, alphaIdx + bytesPerElement, (byte) 0xFF);
                }
            }
            return out;
        }

        if (srcColor)
        {
            if (srcWord)
            {
                for (int i = 0; i < data.length; i += srcBytesPerPixel)
                {
                    int outIdx = i * outCount / srcCount;
                    int red = Util.uint16_t(data[i], data[i + 1]);
                    int green = Util.uint16_t(data[i + 2], data[i + 3]);
                    int blue = Util.uint16_t(data[i + 4], data[i + 5]);
                    int grayscale = Util.toGrayscale(red, green, blue);
                    out[outIdx] = (byte) ((grayscale >> 8) & 0x000000FF);
                    out[outIdx + 1] = (byte) (grayscale & 0x000000FF);
                }
            }
            else
            {
                for (int i = 0; i < data.length; i += srcBytesPerPixel)
                {
                    int outIdx = i * outCount / srcCount;
                    int red = Util.uint8_t(data[i]);
                    int green = Util.uint8_t(data[i + 1]);
                    int blue = Util.uint8_t(data[i + 2]);
                    int grayscale = Util.toGrayscale(red, green, blue);
                    out[outIdx] = (byte) (grayscale & 0x000000FF);
                }
            }
        }
        else
        {
            for (int i = 0; i < data.length; i += srcBytesPerPixel)
            {
                int outIdxR = i * outCount / srcCount;
                int outIdxG = outIdxR + bytesPerElement;
                int outIdxB = outIdxG + bytesPerElement;
                System.arraycopy(data, i, out, outIdxR, srcBytesPerPixel);
                System.arraycopy(data, i, out, outIdxG, srcBytesPerPixel);
                System.arraycopy(data, i, out, outIdxB, srcBytesPerPixel);
            }
        }

        if (outAlpha)
        {
            int outAlphaOffset = (outCount - 1) * bytesPerElement;
            if (srcAlpha)
            {
                int srcAlphaOffset = (srcCount - 1) * bytesPerElement;
                for (int i = 0; i < data.length; i += srcBytesPerPixel)
                {
                    int srcIdx = i + srcAlphaOffset;
                    int outIdx = (i * outCount / srcCount) + outAlphaOffset;
                    System.arraycopy(data, srcIdx, out, outIdx, bytesPerElement);
                }
            }
            else
            {
                for (int i = 0; i < out.length; i += outBytesPerPixel)
                {
                    int outIdx = i + outAlphaOffset;
                    Arrays.fill(out, outIdx, outIdx + bytesPerElement, (byte) 0xFF);
                }
            }
        }

        return out;
    }

    private static byte[] convertSampleDepth(byte[] data, int srcDepth, int outDepth, boolean forceCopy)
    {
        int diff = Math.abs(Math.min(outDepth, 8) - srcDepth);
        if (outDepth > srcDepth)
        {
            int srcMax = ~(0xFF << srcDepth) & 0xFF;
            byte[] out;
            if (outDepth == 16)
            {
                int outMax = ~(0xFFFF << outDepth) & 0xFFFF;
                out = new byte[data.length * 2];
                for (int i = 0; i < data.length; i++)
                {
                    int mapped = Util.map(data[i], 0, srcMax, 0, outMax);
                    out[i * 2] = (byte) ((mapped >> 8) & 0xFF);
                    out[i * 2 + 1] = (byte) (mapped & 0xFF);
                }
            }
            else
            {
                int outMax = ~(0xFFFF << outDepth) & 0xFF;
                out = forceCopy ? new byte[data.length] : data;
                for (int i = 0; i < data.length; i++)
                {
                    out[i] = (byte) Util.map(data[i], 0, srcMax, 0, outMax);
                }
            }
            return out;
        }
        else
        {
            boolean word = srcDepth == 16;
            int mult = word ? 2 : 1;
            byte[] out = word || forceCopy ? new byte[data.length / mult] : data;
            int mask = ~(0xFF << outDepth) & 0xFF;
            for (int i = 0; i < data.length; i += mult)
            {
                out[i / mult] = (byte) ((data[i] >> diff) & mask);
            }
            return out;
        }
    }

    public static int[] convertToPackedColors(Image image, boolean argb, boolean reversed)
    {
        assertRGB8(image);

        int size = image.width() * image.height();
        boolean hasAlpha = image.colorFormat() == ColorFormat.RGB_ALPHA;
        int bytesPerPixel = hasAlpha ? 3 : 4;
        byte[] pixels = image.pixels();
        int[] packedPixels = new int[size];
        if (argb && reversed) // ARGB reversed byte order
        {
            for (int i = 0; i < size; i++)
            {
                int idx = i * bytesPerPixel;
                int alpha = hasAlpha ? (pixels[idx + 3] << 24) : 0xFF000000;
                int packed = alpha | (pixels[idx]) << 16 | (pixels[idx + 1] & 0xFF) << 8 | (pixels[idx + 2] & 0xFF);
                packedPixels[i] = Integer.reverseBytes(packed);
            }
        }
        else if (argb) // ARGB normal byte order
        {
            for (int i = 0; i < size; i++)
            {
                int idx = i * bytesPerPixel;
                int alpha = hasAlpha ? (pixels[idx + 3] << 24) : 0xFF000000;
                int packed = alpha | (pixels[idx]) << 16 | (pixels[idx + 1] & 0xFF) << 8 | (pixels[idx + 2] & 0xFF);
                packedPixels[i] = packed;
            }
        }
        else if (reversed) // RGBA reversed byte order
        {
            for (int i = 0; i < size; i++)
            {
                int idx = i * bytesPerPixel;
                byte alpha = hasAlpha ? pixels[idx + 3] : (byte) 0xFF;
                int packed = pixels[idx] << 24 | (pixels[idx + 1] & 0xFF) << 16 | (pixels[idx + 2] & 0xFF) << 8 | alpha;
                packedPixels[i] = Integer.reverseBytes(packed);
            }
        }
        else // RGBA normal byte order
        {
            for (int i = 0; i < size; i++)
            {
                int idx = i * bytesPerPixel;
                byte alpha = hasAlpha ? pixels[idx + 3] : (byte) 0xFF;
                int packed = pixels[idx] << 24 | (pixels[idx + 1] & 0xFF) << 16 | (pixels[idx + 2] & 0xFF) << 8 | alpha;
                packedPixels[i] = packed;
            }
        }
        return packedPixels;
    }

    public static Image createFromPackedColors(int width, int height, int[] packedColors, boolean argb, boolean reversed)
    {
        int size = width * height;
        if (packedColors.length != size)
        {
            throw new IllegalArgumentException("Malformed pixel array with size %d for image size %dx%d".formatted(
                    packedColors.length, width, height
            ));
        }
        byte[] pixels = new byte[size * 4];
        if (argb && reversed) // ARGB reversed byte order
        {
            for (int i = 0; i < size; i++)
            {
                int idx = i * 4;
                int value = packedColors[i];
                pixels[idx] = (byte) ((value >> 24) & 0x000000FF);
                pixels[idx + 1] = (byte) ((value) & 0x000000FF);
                pixels[idx + 2] = (byte) ((value >> 8) & 0x000000FF);
                pixels[idx + 3] = (byte) ((value >> 16) & 0x000000FF);
            }
        }
        else if (argb) // ARGB normal byte order
        {
            for (int i = 0; i < size; i++)
            {
                int idx = i * 4;
                int value = packedColors[i];
                pixels[idx] = (byte) ((value >> 16) & 0x000000FF);
                pixels[idx + 1] = (byte) ((value >> 8) & 0x000000FF);
                pixels[idx + 2] = (byte) ((value) & 0x000000FF);
                pixels[idx + 3] = (byte) ((value >> 24) & 0x000000FF);
            }
        }
        else if (reversed) // RGBA reversed byte order
        {
            for (int i = 0; i < packedColors.length; i++)
            {
                Util.intToBytes(pixels, i * 4, Integer.reverseBytes(packedColors[i]));
            }
        }
        else // RGBA normal byte order
        {
            for (int i = 0; i < packedColors.length; i++)
            {
                Util.intToBytes(pixels, i * 4, packedColors[i]);
            }
        }
        return new Image(width, height, ColorFormat.RGB_ALPHA, 8, pixels);
    }

    public static void assertValidFormat(int width, int height, ColorFormat format, int sampleDepth, byte[] pixels)
    {
        if (format == ColorFormat.PALETTE)
        {
            throw new IllegalArgumentException("Palette color format is not valid for in-memory images");
        }

        if (!format.isValidSampleDepth(sampleDepth))
        {
            throw new IllegalArgumentException("Invalid sample depth " + sampleDepth + " for format " + format);
        }

        int bytesPerPixel = format.getBytePerPixel(Math.max(sampleDepth / 8, 1), false);
        int totalBytes = width * height * bytesPerPixel;
        if (pixels.length != totalBytes)
        {
            throw new IllegalArgumentException(
                    "Malformed pixel data for format %s at sample depth %d. Expected size: %d, actual size: %d".formatted(
                            format, sampleDepth, totalBytes, pixels.length
                    )
            );
        }
    }

    public static int getPixel(Image image, int x, int y, boolean argb)
    {
        assertRGB8(image);

        byte[] pixels = image.pixels();
        boolean hasAlpha = image.colorFormat().isAlphaUsed();
        int idx = (y * image.width() + x) * (hasAlpha ? 4 : 3);
        if (argb)
        {
            int alpha = hasAlpha ? (pixels[idx + 3] << 24) : 0xFF000000;
            return alpha | (pixels[idx]) << 16 | (pixels[idx + 1] & 0xFF) << 8 | (pixels[idx + 2] & 0xFF);
        }
        else
        {
            byte alpha = hasAlpha ? pixels[idx + 3] : (byte) 0xFF;
            return pixels[idx] << 24 | (pixels[idx + 1] & 0xFF) << 16 | (pixels[idx + 2] & 0xFF) << 8 | alpha;
        }
    }

    public static void setPixel(Image image, int x, int y, int color, boolean argb)
    {
        assertRGB8(image);

        byte[] pixels = image.pixels();
        boolean hasAlpha = image.colorFormat().isAlphaUsed();
        int idx = (y * image.width() + x) * (hasAlpha ? 4 : 3);
        if (argb)
        {
            pixels[idx] = Util.uint8_t(color >> 16);
            pixels[idx + 1] = Util.uint8_t(color >> 8);
            pixels[idx + 2] = Util.uint8_t(color);
            if (hasAlpha)
            {
                pixels[idx + 3] = Util.uint8_t(color >> 24);
            }
        }
        else
        {
            pixels[idx] = Util.uint8_t(color >> 24);
            pixels[idx + 1] = Util.uint8_t(color >> 16);
            pixels[idx + 2] = Util.uint8_t(color >> 8);
            if (hasAlpha)
            {
                pixels[idx + 3] = Util.uint8_t(color);
            }
        }
    }

    private static void assertRGB8(Image image)
    {
        if (!image.colorFormat().isColorUsed() || image.sampleDepth() != 8)
        {
            throw new IllegalStateException("Only 8bit RGB and RGBA formats can be converted to and from packed pixels");
        }
    }



    private FormatConverter() { }
}
