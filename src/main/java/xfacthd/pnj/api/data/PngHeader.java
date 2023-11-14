package xfacthd.pnj.api.data;

import xfacthd.pnj.api.define.ColorFormat;
import xfacthd.pnj.api.define.CompressionMethod;
import xfacthd.pnj.api.define.FilterMethod;
import xfacthd.pnj.api.define.InterlaceMethod;

/**
 * Container for the header metadata of a PNG file
 * @param width The width of the image in pixels
 * @param height The height of the image in pixels
 * @param colorFormat The {@link ColorFormat} of the image
 * @param bitDepth The sample depth of the image in bits per element
 * @param compression The compression method used to compress the pixel data
 * @param filter The scanline pre-filter method applied to the scanlines
 * @param interlace The interlace method applied to the scanlines
 * @apiNote The format and bit depth may be different from the format and sample depth of a fully decoded image,
 *          due to de-palettization and additional processing such as transparency and background insertion
 */
public record PngHeader(
        int width,
        int height,
        ColorFormat colorFormat,
        int bitDepth,
        CompressionMethod compression,
        FilterMethod filter,
        InterlaceMethod interlace
)
{ }
