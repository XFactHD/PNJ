package io.github.xfacthd.pnj.impl.encoder.chunkencoder;

import io.github.xfacthd.pnj.api.define.ColorFormat;
import io.github.xfacthd.pnj.api.define.EncoderOption;
import io.github.xfacthd.pnj.impl.define.AdaptiveFilterType;
import io.github.xfacthd.pnj.impl.define.ChunkType;
import io.github.xfacthd.pnj.impl.encoder.PNJEncoderImpl;
import io.github.xfacthd.pnj.impl.encoder.data.EncodingImage;
import io.github.xfacthd.pnj.impl.util.OptionSet;
import io.github.xfacthd.pnj.impl.util.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Deflater;

public final class PixelEncoder
{
    private static final int INPUT_BUF_SIZE = 16384;
    private static final int OUTPUT_BUF_SIZE = 8192;

    public static void encode(OutputStream stream, EncodingImage image, OptionSet<EncoderOption> optionSet) throws IOException
    {
        byte[] inBuf = new byte[INPUT_BUF_SIZE];
        byte[] outBuf = new byte[OUTPUT_BUF_SIZE];
        int compressionLevel = getCompressionLevel(optionSet);
        Deflater deflater = new Deflater(compressionLevel);
        int pointer;

        boolean palette = image.getColorFormat() == ColorFormat.PALETTE;
        int depth = palette ? image.getBitDepth() : image.getSampleDepth();
        if (depth >= 8)
        {
            pointer = encodeByteOrWord(stream, image, palette, depth, inBuf, outBuf, deflater);
        }
        else
        {
            pointer = encodeSubByte(stream, image, depth, inBuf, outBuf, deflater);
        }

        deflater.finish();
        compressIfNeeded(stream, deflater, inBuf, outBuf, pointer, true);
        deflater.end();
    }

    private static int getCompressionLevel(OptionSet<EncoderOption> optionSet)
    {
        if (optionSet.contains(EncoderOption.BEST_COMPRESSION))
        {
            return Deflater.BEST_COMPRESSION;
        }
        if (optionSet.contains(EncoderOption.FAST_COMPRESSION))
        {
            return Deflater.BEST_SPEED;
        }
        if (optionSet.contains(EncoderOption.NO_COMPRESSION))
        {
            return Deflater.NO_COMPRESSION;
        }
        return Deflater.DEFAULT_COMPRESSION;
    }

    private static int encodeByteOrWord(
            OutputStream stream, EncodingImage image, boolean palette, int depth, byte[] inBuf, byte[] outBuf, Deflater deflater
    ) throws IOException
    {
        int bytePerPixelNoAlpha;
        int bytePerPixelMaybeAlpha;
        if (palette)
        {
            bytePerPixelNoAlpha = 1;
            bytePerPixelMaybeAlpha = 1;
        }
        else
        {
            ColorFormat format = image.getColorFormat();
            bytePerPixelNoAlpha = format.getBytePerPixel(depth / 8, false);
            boolean hadAlpha = image.getTransparency() != null;
            bytePerPixelMaybeAlpha = hadAlpha ? format.getBytePerPixel(depth / 8, true) : bytePerPixelNoAlpha;
        }
        int lineWidth = image.getWidth() * bytePerPixelMaybeAlpha;

        int pointer = 0;
        byte[] pixels = image.getPixels();
        for (int i = 0; i < pixels.length; i += bytePerPixelMaybeAlpha)
        {
            if (i % lineWidth == 0)
            {
                inBuf[pointer] = Util.uint8_t(AdaptiveFilterType.NONE.ordinal());
                pointer++;
                pointer = compressIfNeeded(stream, deflater, inBuf, outBuf, pointer, false);
            }

            for (int j = 0; j < bytePerPixelNoAlpha; j++)
            {
                inBuf[pointer] = pixels[i + j];
                pointer++;
                pointer = compressIfNeeded(stream, deflater, inBuf, outBuf, pointer, false);
            }
        }
        return pointer;
    }

    private static int encodeSubByte(
            OutputStream stream, EncodingImage image, int depth, byte[] inBuf, byte[] outBuf, Deflater deflater
    ) throws IOException
    {
        int width = image.getWidth();
        int pixelsPerByte = 8 / depth;

        int pointer = 0;
        byte[] pixels = image.getPixels();
        for (int y = 0; y < image.getHeight(); y++)
        {
            inBuf[pointer] = Util.uint8_t(AdaptiveFilterType.NONE.ordinal());
            pointer++;
            pointer = compressIfNeeded(stream, deflater, inBuf, outBuf, pointer, false);

            for (int x = 0; x < width; x += pixelsPerByte)
            {
                byte data = 0;
                for (int i = 0; i < pixelsPerByte && x + i < width; i++)
                {
                    int idx = y * width + x + i;
                    int offset = (pixelsPerByte - 1 - i) * depth;
                    data |= (byte) (pixels[idx] << offset);
                }
                inBuf[pointer] = data;
                pointer++;
                pointer = compressIfNeeded(stream, deflater, inBuf, outBuf, pointer, false);
            }
        }
        return pointer;
    }

    private static int compressIfNeeded(
            OutputStream stream, Deflater deflater, byte[] buf, byte[] outBuf, int pointer, boolean finish
    ) throws IOException
    {
        if (finish || pointer >= INPUT_BUF_SIZE)
        {
            deflater.setInput(buf, 0, pointer);
            // Deflater#needsInput() may still return true after calling Deflater#finish() despite being irrelevant with
            // flush mode FINISH which is automatically used after a Deflater#finish() call
            while (!deflater.finished() && (!deflater.needsInput() || finish))
            {
                int len = deflater.deflate(outBuf);
                if (len > 0)
                {
                    PNJEncoderImpl.encodeChunk(stream, ChunkType.IDAT, outBuf, len);
                }
            }
            return 0;
        }
        return pointer;
    }



    private PixelEncoder() { }
}
