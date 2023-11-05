package xfacthd.pnj.api.data;

import xfacthd.pnj.api.define.ColorFormat;
import xfacthd.pnj.impl.util.FormatConverter;

/**
 * In-memory representation of an image which was just decoded from a PNG or is about to be encoded to PNG
 * @param width The width of the image in pixels
 * @param height The height of the image in pixels
 * @param colorFormat The {@link ColorFormat} of the image
 * @param sampleDepth The sample depth of the image in bits per element
 * @param pixels The pixel data of the image in raw bytes
 * @apiNote {@link ColorFormat#PALETTE} is not a valid in-memory format. The encoder will select it as a color
 *          format for the resulting file at its own discretion
 */
public record Image(int width, int height, ColorFormat colorFormat, int sampleDepth, byte[] pixels)
{
    public Image
    {
        FormatConverter.assertValidFormat(width, height, colorFormat, sampleDepth, pixels);
    }

    /**
     * Convert this image to RGB with a sample depth of 8 bit
     */
    public Image toRGB8()
    {
        return toFormat(ColorFormat.RGB, 8);
    }

    /**
     * Convert this image to RGBA with a sample depth of 8 bit
     */
    public Image toRGBA8()
    {
        return toFormat(ColorFormat.RGB_ALPHA, 8);
    }

    /**
     * Convert this image to the given format at the given sample depth
     * @throws IllegalArgumentException when the given format cannot be used for in-memory images or the given
     * sample depth is invalid for the format
     */
    public Image toFormat(ColorFormat colorFormat, int sampleDepth)
    {
        return FormatConverter.toFormat(this, colorFormat, sampleDepth);
    }

    /**
     * Convert the pixel byte array to an array of integers with packed colors
     * @param argb If true, the colors will be packed as ARGB, else the colors will be packed as RGBA
     */
    public int[] toPackedPixels(boolean argb)
    {
        return FormatConverter.convertToPackedColors(this, argb);
    }



    /**
     * Create an {@link Image} from an array of integers holding packed colors
     * @param width The width of the image
     * @param height The height of the image
     * @param packedPixels The packed colors
     * @param argb If true, the packed colors will be interpreted as ARGB, else they will be interpreted as RGBA
     */
    public static Image fromPackedPixels(int width, int height, int[] packedPixels, boolean argb)
    {
        return FormatConverter.createFromPackedColors(width, height, packedPixels, argb);
    }
}
