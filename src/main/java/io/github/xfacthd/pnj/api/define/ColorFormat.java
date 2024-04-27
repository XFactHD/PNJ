package io.github.xfacthd.pnj.api.define;

import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

/**
 * The color formats supported by the PNG file format
 */
public enum ColorFormat
{
    GRAYSCALE       (0, 1, d -> d, d -> d == 1 || d == 2 || d == 4 || d == 8 || d == 16),
    RGB             (2, 3, d -> d, d -> d == 8 || d == 16),
    PALETTE         (3, 3, d -> 8, d -> d == 1 || d == 2 || d == 4 || d == 8),
    GRAYSCALE_ALPHA (4, 2, d -> d, d -> d == 8 || d == 16),
    RGB_ALPHA       (6, 4, d -> d, d -> d == 8 || d == 16),
    ;

    private static final int MAX_IDX = 6;
    private static final ColorFormat[] FORMATS = makeFormatLookup();

    private final int typeCode;
    private final int elemCount;
    private final IntPredicate bitDepthValidation;
    private final IntUnaryOperator bitDepthToSampleDepth;
    private final boolean paletteUsed;
    private final boolean colorUsed;
    private final boolean alphaUsed;

    ColorFormat(int typeCode, int elemCount, IntUnaryOperator bitDepthToSampleDepth, IntPredicate bitDepthValidation)
    {
        this.typeCode = typeCode;
        this.elemCount = elemCount;
        this.bitDepthToSampleDepth = bitDepthToSampleDepth;
        this.bitDepthValidation = bitDepthValidation;
        this.paletteUsed = (typeCode & 0x1) != 0;
        this.colorUsed = (typeCode & 0x2) != 0;
        this.alphaUsed = (typeCode & 0x4) != 0;
    }

    /**
     * {@return the type code used to specify this format in the PNG header}
     */
    public int getTypeCode()
    {
        return typeCode;
    }

    /**
     * {@return the amount of color elements used by this format in the decoded pixel representation}
     */
    public int getElementCount()
    {
        return elemCount;
    }

    /**
     * Calculates the amount of bytes needed per pixel for the given element size
     * @param bytePerElement The amount of bytes used per color element
     * @param addAlpha Whether an alpha channel should be added if the color format doesn't already have one
     * @return the amount of bytes per pixel
     */
    public int getBytePerPixel(int bytePerElement, boolean addAlpha)
    {
        int bytes = elemCount * bytePerElement;
        if (!alphaUsed && addAlpha)
        {
            bytes += bytePerElement;
        }
        return bytes;
    }

    /**
     * {@return true if the given bit depth is valid for this color format}
     * @apiNote this is relevant mainly for the palette format as it differentiates between bit depth and sample depth,
     *          using the former for the size of the palette index and the latter for the actual color from the palette
     */
    public boolean isValidBitDepth(int depth)
    {
        return bitDepthValidation.test(depth);
    }

    /**
     * {@return true of the given sample depth is valid for this color format}
     */
    public boolean isValidSampleDepth(int depth)
    {
        if (bitDepthToSampleDepth.applyAsInt(depth) != depth)
        {
            return false;
        }
        return bitDepthValidation.test(depth);
    }

    /**
     * {@return the sample depth corresponding to the given bit depth}
     */
    public int getSampleDepthFromBitDepth(int bitDepth)
    {
        return bitDepthToSampleDepth.applyAsInt(bitDepth);
    }

    /**
     * {@return true if this format requires a palette}
     */
    public boolean isPaletteUsed()
    {
        return paletteUsed;
    }

    /**
     * {@return true if this format uses RGB}
     */
    public boolean isColorUsed()
    {
        return colorUsed;
    }

    /**
     * {@return true if this format contains an alpha channel}
     */
    public boolean isAlphaUsed()
    {
        return alphaUsed;
    }



    /**
     * {@return the color format corresponding to the given type code}
     */
    public static ColorFormat decode(byte typeCode)
    {
        if (typeCode >= 0 && typeCode <= MAX_IDX)
        {
            ColorFormat type = FORMATS[typeCode];
            if (type != null)
            {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid color format: " + typeCode);
    }

    private static ColorFormat[] makeFormatLookup()
    {
        ColorFormat[] lookup = new ColorFormat[MAX_IDX + 1];
        for (ColorFormat format : values())
        {
            lookup[format.typeCode] = format;
        }
        return lookup;
    }
}
