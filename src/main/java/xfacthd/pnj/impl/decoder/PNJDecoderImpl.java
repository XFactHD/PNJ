package xfacthd.pnj.impl.decoder;

import xfacthd.pnj.api.data.Image;
import xfacthd.pnj.api.data.PngHeader;
import xfacthd.pnj.api.define.DecoderOption;
import xfacthd.pnj.impl.data.*;
import xfacthd.pnj.impl.decoder.chunkdecoder.*;
import xfacthd.pnj.impl.decoder.chunkdecoder.pixel.Decompressor;
import xfacthd.pnj.impl.decoder.chunkdecoder.pixel.PixelDecoder;
import xfacthd.pnj.impl.decoder.data.DecodingImage;
import xfacthd.pnj.impl.decoder.postprocessor.*;
import xfacthd.pnj.impl.define.*;
import xfacthd.pnj.impl.util.OptionSet;
import xfacthd.pnj.impl.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public final class PNJDecoderImpl
{
    public static Image decode(Path path, DecoderOption... options) throws IOException
    {
        try (InputStream stream = Files.newInputStream(path))
        {
            return decode(stream, options);
        }
    }

    public static Image decode(InputStream stream, DecoderOption... options) throws IOException
    {
        try
        {
            OptionSet<DecoderOption> optionSet = new OptionSet<>(options);

            byte[] magic = stream.readNBytes(8);
            if (!Arrays.equals(magic, Constants.PNG_MAGIC))
            {
                throw new IOException("PNG magic doesn't match");
            }

            ChunkList chunks = new ChunkList();
            while (stream.available() > 0)
            {
                if (!readChunk(stream, chunks))
                {
                    break;
                }
            }

            Chunk endChunk = chunks.get(chunks.size() - 1);
            if (endChunk == null || endChunk.type() != ChunkType.IEND)
            {
                throw new IOException("Unexpected final chunk, expected IEND: %s".formatted(
                        endChunk == null ? null : endChunk.type()
                ));
            }
            if (endChunk.data().length != 0)
            {
                throw new IOException("Unexpected data in IEND chunk, should be empty");
            }

            if (!chunks.containsType(ChunkType.IDAT))
            {
                throw new IOException("Missing pixel data");
            }

            DecodingImage image = HeaderDecoder.decode(chunks, optionSet);

            for (int i = 1; i < chunks.size(); i++)
            {
                Chunk chunk = chunks.get(i);
                switch (chunk.type())
                {
                    case PLTE -> PaletteDecoder.decode(image, chunk);
                    case bKGD -> BackgroundDecoder.decode(image, chunk, optionSet);
                    case tRNS -> TransparencyDecoder.decode(image, chunk, optionSet);
                }
            }

            // Pixel data decoding
            PixelDecoder decoder = PixelDecoder.from(image);
            Decompressor.decompressInto(chunks, decoder);

            // Pixel data post-processing according to ancillary data
            TransparencyPostProcessor.process(image, optionSet);
            PalettePostProcessor.process(image);
            BackgroundPostProcessor.process(image, optionSet);

            return image.finish();
        }
        catch (IOException e)
        {
            throw e;
        }
        catch (Throwable t)
        {
            throw new IOException("Encountered an error while decoding PNG", t);
        }
    }

    public static PngHeader decodeHeaderOnly(Path path) throws IOException
    {
        try (InputStream stream = Files.newInputStream(path))
        {
            return decodeHeaderOnly(stream);
        }
    }

    public static PngHeader decodeHeaderOnly(InputStream stream) throws IOException
    {
        try
        {
            byte[] magic = stream.readNBytes(8);
            if (!Arrays.equals(magic, Constants.PNG_MAGIC))
            {
                throw new IOException("PNG magic doesn't match");
            }

            ChunkList chunks = new ChunkList();

            readChunk(stream, chunks);
            if (chunks.isEmpty())
            {
                throw new IOException("Failed to read first chunk");
            }

            Chunk chunk = chunks.get(0);
            if (chunk.type() != ChunkType.IHDR)
            {
                throw new IOException("First chunk must be an IHDR chunk");
            }

            return HeaderDecoder.decodeHeaderOnly(chunk);
        }
        catch (IOException e)
        {
            throw e;
        }
        catch (Throwable t)
        {
            throw new IOException("Encountered an error while decoding PNG header", t);
        }
    }

    private static boolean readChunk(InputStream stream, ChunkList chunks) throws IOException
    {
        int chunkLen = Util.intFromBytes(stream.readNBytes(4));
        byte[] typeCode = stream.readNBytes(4);
        byte[] chunkData = stream.readNBytes(chunkLen);
        int chunkCrc = Util.intFromBytes(stream.readNBytes(4));
        if (!Util.validateCRC(chunkCrc, typeCode, chunkData))
        {
            throw new IOException("Encountered chunk with broken CRC");
        }

        ChunkType chunkType = ChunkType.decode(typeCode);
        if (chunkType == null)
        {
            // Ignore unknown non-mandatory chunks
            return true;
        }
        if (!chunkType.isValidPosition(chunks))
        {
            throw new IOException("Encountered chunk type %s in incorrect position".formatted(chunkType.getName()));
        }

        if (chunkType.isSupported())
        {
            chunks.add(new Chunk(chunkType, chunkData));
        }
        else
        {
            // Immediately discard ignored chunks, no point in keeping them in memory,
            // but mark them as seen for ordering checks
            chunks.markType(chunkType);
        }
        return chunkType != ChunkType.IEND;
    }



    private PNJDecoderImpl() { }
}
