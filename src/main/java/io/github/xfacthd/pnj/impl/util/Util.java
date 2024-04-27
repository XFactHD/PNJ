package io.github.xfacthd.pnj.impl.util;

import java.util.zip.CRC32;

public final class Util
{
    public static int intFromBytes(byte[] bytes)
    {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    public static int intFromBytes(byte[] bytes, int offset)
    {
        return bytes[offset] << 24 | (bytes[offset + 1] & 0xFF) << 16 | (bytes[offset + 2] & 0xFF) << 8 | (bytes[offset + 3] & 0xFF);
    }

    public static void intToBytes(byte[] bytes, int offset, int value)
    {
        bytes[offset] = (byte) ((value >> 24) & 0x000000FF);
        bytes[offset + 1] = (byte) ((value >> 16) & 0x000000FF);
        bytes[offset + 2] = (byte) ((value >> 8) & 0x000000FF);
        bytes[offset + 3] = (byte) ((value) & 0x000000FF);
    }

    public static byte[] intToBytes(int value)
    {
        byte[] bytes = new byte[4];
        intToBytes(bytes, 0, value);
        return bytes;
    }

    public static long calculateCRC(byte[] typeCode, byte[] chunkData, int len)
    {
        CRC32 crc32 = new CRC32();
        crc32.update(typeCode);
        crc32.update(chunkData, 0, len);
        return crc32.getValue();
    }

    public static boolean validateCRC(int chunkCrc, byte[] typeCode, byte[] chunkData)
    {
        return calculateCRC(typeCode, chunkData, chunkData.length) == Integer.toUnsignedLong(chunkCrc);
    }

    public static int paethPredictor(int a, int b, int c)
    {
        int p = a + b - c;
        int pa = Math.abs(p - a);
        int pb = Math.abs(p - b);
        int pc = Math.abs(p - c);
        if (pa <= pb && pa <= pc)
        {
            return a;
        }
        if (pb <= pc)
        {
            return b;
        }
        return c;
    }

    public static byte uint8_t(int value)
    {
        return (byte) (value & 0x000000FF);
    }

    public static int uint8_t(byte value)
    {
        return ((int) value) & 0x000000FF;
    }

    public static int uint16_t(byte high, byte low)
    {
        int iLow = ((int) low) & 0x000000FF;
        int iHigh = ((int) high) & 0x000000FF;
        return iLow | (iHigh << 8);
    }

    public static int toGrayscale(int red, int green, int blue)
    {
        double dRed = .299 * red;
        double dGreen = .587 * green;
        double dBlue = .114 * blue;
        return (int) Math.round(dRed + dGreen + dBlue);
    }

    public static int map(int x, int inMin, int inMax, int outMin, int outMax)
    {
        int divisor = inMax - inMin;
        return ((x - inMin) * (outMax - outMin) + (divisor / 2)) / divisor + outMin;
    }

    public static int getBytesPerLine(int pixels, int precision, int channels)
    {
        if (precision < 8)
        {
            return (pixels + ((8 / precision) - 1)) / (8 / precision);
        }
        else
        {
            return channels * (precision / 8) * pixels;
        }
    }

    public static byte[] getFullAlphaForDepth(int depth)
    {
        int bytesPerElement = Math.max(depth / 8, 1);
        byte[] alphaFull = new byte[bytesPerElement];
        if (bytesPerElement == 2)
        {
            alphaFull[0] = (byte) 0xFF;
            alphaFull[1] = (byte) 0xFF;
        }
        else
        {
            alphaFull[0] = (byte) ((1 << depth) - 1);
        }
        return alphaFull;
    }



    private Util() { }
}
