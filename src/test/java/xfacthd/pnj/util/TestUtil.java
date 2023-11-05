package xfacthd.pnj.util;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import xfacthd.pnj.api.data.Image;
import xfacthd.pnj.api.define.ColorFormat;
import xfacthd.pnj.impl.util.Util;

import java.io.IOException;
import java.nio.*;
import java.nio.file.*;

public final class TestUtil
{
    public static void writeToPPM(Path path, Image image) throws IOException
    {
        ColorFormat colorFormat = image.colorFormat();
        if (!colorFormat.isColorUsed())
        {
            colorFormat = colorFormat.isAlphaUsed() ? ColorFormat.RGB_ALPHA : ColorFormat.RGB;
            int depth = Math.max(image.sampleDepth(), 8);
            image = image.toFormat(colorFormat, depth);
        }

        StringBuilder ppm = new StringBuilder("P3\n");
        ppm.append(image.width()).append(" ").append(image.height()).append("\n");
        ppm.append((1 << image.sampleDepth()) - 1).append("\n");

        byte[] data = image.pixels();
        int bytePerElem = Math.max(image.sampleDepth() / 8, 1);
        int byteCount = colorFormat.getBytePerPixel(bytePerElem, false);
        int elemOff = image.sampleDepth() / 8;
        boolean word = elemOff == 2;

        int charCount = 0;
        for (int i = 0; i < data.length; i += byteCount)
        {
            if (word)
            {
                ppm.append(Util.uint16_t(data[i], data[i + 1]))
                        .append(" ")
                        .append(Util.uint16_t(data[i + elemOff], data[i + elemOff + 1]))
                        .append(" ")
                        .append(Util.uint16_t(data[i + (elemOff * 2)], data[i + (elemOff * 2) + 1]))
                        .append(" ");
            }
            else
            {
                ppm.append(Util.uint8_t(data[i]))
                        .append(" ")
                        .append(Util.uint8_t(data[i + elemOff]))
                        .append(" ")
                        .append(Util.uint8_t(data[i + (elemOff * 2)]))
                        .append(" ");
            }

            charCount += 12;
            if (charCount > 70)
            {
                charCount = 0;
                ppm.append("\n");
            }
        }

        Files.writeString(path, ppm.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static Image loadComparisonImage(Path path, int depth)
    {
        int width;
        int height;
        int channels;
        byte[] pixels;
        try (MemoryStack memorystack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = memorystack.mallocInt(1);
            IntBuffer heightBuf = memorystack.mallocInt(1);
            IntBuffer channelsBuf = memorystack.mallocInt(1);
            if (depth == 16)
            {
                ShortBuffer pixBuf = STBImage.stbi_load_16(path.toString(), widthBuf, heightBuf, channelsBuf, 0);
                if (pixBuf == null)
                {
                    System.err.println(path + ": Comparison 16bit load failed: " + STBImage.stbi_failure_reason());
                    return null;
                }

                pixels = new byte[pixBuf.limit() * 2];
                for (int i = 0; i < pixels.length; i += 2)
                {
                    short val = pixBuf.get(i / 2);
                    pixels[i] = (byte) ((val >> 8) & 0xFF);
                    pixels[i + 1] = (byte) (val & 0xFF);
                }
                STBImage.stbi_image_free(pixBuf);
            }
            else
            {
                ByteBuffer pixBuf = STBImage.stbi_load(path.toString(), widthBuf, heightBuf, channelsBuf, 0);
                if (pixBuf == null)
                {
                    System.err.println(path + ": Comparison 8bit load failed: " + STBImage.stbi_failure_reason());
                    return null;
                }

                pixels = new byte[pixBuf.limit()];
                pixBuf.get(0, pixels);
                STBImage.stbi_image_free(pixBuf);
            }
            width = widthBuf.get();
            height = heightBuf.get();
            channels = channelsBuf.get();
        }

        ColorFormat format = switch (channels)
        {
            case 1 -> ColorFormat.GRAYSCALE;
            case 2 -> ColorFormat.GRAYSCALE_ALPHA;
            case 3 -> ColorFormat.RGB;
            case 4 -> ColorFormat.RGB_ALPHA;
            default -> throw new IllegalStateException("Invalid channel count: " + channels);
        };
        int outDepth = depth;
        if (outDepth < 8 && !format.isValidSampleDepth(outDepth))
        {
            outDepth = 8;
        }
        return new Image(width, height, format, Math.max(depth, 8), pixels).toFormat(format, outDepth);
    }

    public static String rangeToString(byte[] bytes, int start, int count, int bytesPerElem)
    {
        StringBuilder builder = new StringBuilder("[ ");
        if (bytesPerElem == 2)
        {
            for (int i = start; i < (start + count); i += 2)
            {
                int value = Util.uint16_t(bytes[i], bytes[i + 1]);
                builder.append("%5d ".formatted(value));
            }
        }
        else
        {
            for (int i = start; i < (start + count); i++)
            {
                int value = Util.uint8_t(bytes[i]);
                builder.append("%3d ".formatted(value));
            }
        }
        return builder.append("]").toString();
    }

    public static String throwableToString(Throwable t)
    {
        while (t.getCause() != null)
        {
            t = t.getCause();
        }
        return "%s: '%s'".formatted(t.getClass().getSimpleName(), t.getMessage());
    }



    private TestUtil() { }
}
