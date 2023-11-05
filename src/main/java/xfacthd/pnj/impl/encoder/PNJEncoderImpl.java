package xfacthd.pnj.impl.encoder;

import xfacthd.pnj.api.data.Image;
import xfacthd.pnj.impl.define.*;
import xfacthd.pnj.impl.encoder.chunkencoder.*;
import xfacthd.pnj.impl.encoder.data.EncodingImage;
import xfacthd.pnj.impl.encoder.preprocessor.PaletteExtractor;
import xfacthd.pnj.impl.encoder.preprocessor.TransparencyExtractor;
import xfacthd.pnj.impl.util.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;

public final class PNJEncoderImpl
{
    private static final byte[] EMPTY = new byte[0];

    public static void encode(Path path, Image image, OpenOption... openOptions) throws IOException
    {
        try (OutputStream stream = Files.newOutputStream(path, openOptions))
        {
            encode(stream, image);
        }
    }

    public static void encode(OutputStream stream, Image image) throws IOException
    {
        EncodingImage encodingImage = new EncodingImage(image);

        PaletteExtractor.process(encodingImage);
        TransparencyExtractor.process(encodingImage);

        stream.write(Constants.PNG_MAGIC);
        HeaderEncoder.encode(stream, encodingImage);
        PaletteEncoder.encode(stream, encodingImage);
        TransparencyEncoder.encode(stream, encodingImage);
        PixelEncoder.encode(stream, encodingImage);
        encodeChunk(stream, ChunkType.IEND, EMPTY, 0);
    }

    public static void encodeChunk(OutputStream stream, ChunkType type, byte[] data, int len) throws IOException
    {
        byte[] typeCode = type.getTypeCode();
        byte[] crc = Util.intToBytes((int) (Util.calculateCRC(typeCode, data, len) & 0x00000000FFFFFFFFL));

        stream.write(Util.intToBytes(len));
        stream.write(typeCode);
        stream.write(data, 0, len);
        stream.write(crc);
    }



    private PNJEncoderImpl() { }
}
