package xfacthd.pnj.api;

import xfacthd.pnj.api.define.DecoderOption;
import xfacthd.pnj.impl.decoder.PNJDecoderImpl;
import xfacthd.pnj.impl.encoder.PNJEncoderImpl;
import xfacthd.pnj.api.data.Image;

import java.io.*;
import java.nio.file.OpenOption;
import java.nio.file.Path;

/**
 * The main entrypoint to the PNG decoder and encoder
 */
public final class PNJ
{
    /**
     * Decode the PNG file at the given {@link Path} to an {@link Image} with the given
     * {@linkplain DecoderOption decoder options}
     * @param pngPath The path of the PNG file
     * @param options The options for the decoder
     * @return an {@link Image} containing the decoded pixel data
     */
    public static Image decode(Path pngPath, DecoderOption... options) throws IOException
    {
        return PNJDecoderImpl.decode(pngPath, options);
    }

    /**
     * Decode the PNG file from the given {@link InputStream} to an {@link Image} with the given
     * {@linkplain DecoderOption decoder options}
     * @param pngStream The stream providing the PNG file contents
     * @param options The options for the decoder
     * @return an {@link Image} containing the decoded pixel data
     */
    public static Image decode(InputStream pngStream, DecoderOption... options) throws IOException
    {
        return PNJDecoderImpl.decode(pngStream, options);
    }

    /**
     * Encode the given {@link Image} to a PNG file and save it at the given {@link Path}
     * @param pngPath The output path to save the PNG at
     * @param image The image to be encoded
     * @param openOptions The options to use when creating the output file
     */
    public static void encode(Path pngPath, Image image, OpenOption... openOptions) throws IOException
    {
        PNJEncoderImpl.encode(pngPath, image, openOptions);
    }

    /**
     * Encode the given {@link Image} to a PNG file and write it to the given {@link OutputStream}
     * @param pngStream The stream to write the PNG to
     * @param image The image to be encoded
     */
    public static void encode(OutputStream pngStream, Image image) throws IOException
    {
        PNJEncoderImpl.encode(pngStream, image);
    }



    private PNJ() { }
}
