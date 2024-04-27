package io.github.xfacthd.pnj.api.data;

import io.github.xfacthd.pnj.api.define.ColorFormat;
import io.github.xfacthd.pnj.impl.util.FormatConverter;

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
     * @param reversed If true, the colors will be packed in reversed byte order (i.e. BGRA and ABGR respectively)
     * @apiNote Only images with 8bit RGB or RGBA format can be converted to packed colors
     */
    public int[] toPackedPixels(boolean argb, boolean reversed)
    {
        return FormatConverter.convertToPackedColors(this, argb, reversed);
    }

    /**
     * Get the packed color of the pixel at the given coordinate
     * @param x The X coordinate of the target pixel
     * @param y The Y coordinate of the target pixel
     * @param argb If true, the color will be packed as ARGB, else it will be packed as RGBA
     * @apiNote Only images with 8bit RGB or RGBA format can be converted to and from packed colors
     */
    public int getPixel(int x, int y, boolean argb)
    {
        return FormatConverter.getPixel(this, x, y, argb);
    }

    /**
     * Set the color of the pixel at the given coordinate to the given packed color
     * @param x The X coordinate of the target pixel
     * @param y The Y coordinate of the target pixel
     * @param color The packed color value to write to the target pixel
     * @param argb If true, the packed color will be interpreted as ARGB, else it will be interpreted as RGBA
     * @apiNote Only images with 8bit RGB or RGBA format can be converted to and from packed colors
     */
    public void setPixel(int x, int y, int color, boolean argb)
    {
        FormatConverter.setPixel(this, x, y, color, argb);
    }



    /**
     * Create an {@link Image} from an array of integers holding packed colors
     * @param width The width of the image
     * @param height The height of the image
     * @param packedPixels The packed colors
     * @param argb If true, the packed colors will be interpreted as ARGB, else they will be interpreted as RGBA
     * @param reversed If true, the packed colors will be interpreted in reversed byte order (i.e. BGRA and ABGR respectively)
     */
    public static Image fromPackedPixels(int width, int height, int[] packedPixels, boolean argb, boolean reversed)
    {
        return FormatConverter.createFromPackedColors(width, height, packedPixels, argb, reversed);
    }
}
