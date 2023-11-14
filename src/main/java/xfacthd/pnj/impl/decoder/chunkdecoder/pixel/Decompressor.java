package xfacthd.pnj.impl.decoder.chunkdecoder.pixel;

import xfacthd.pnj.impl.define.ChunkType;
import xfacthd.pnj.impl.data.*;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public final class Decompressor
{
    public static int decompressInto(ChunkList chunks, PixelDecoder decoder, int startIdx) throws IOException
    {
        Inflater inflater = new Inflater();
        byte[] result = new byte[32768];
        int idx = startIdx;
        for (; idx < chunks.size(); idx++)
        {
            Chunk chunk = chunks.get(idx);
            if (chunk.type() != ChunkType.IDAT)
            {
                break;
            }

            inflater.setInput(chunk.data());

            try
            {
                int count;
                while ((count = inflater.inflate(result)) > 0)
                {
                    decoder.decode(result, count);
                }
            }
            catch (DataFormatException e)
            {
                throw new IOException("Encountered invalid compression: invalid format", e);
            }
            if (inflater.needsDictionary())
            {
                throw new IOException("Encountered invalid compression: dictionary requested");
            }
        }
        if (!inflater.finished())
        {
            throw new IOException("Compressed data stream incomplete");
        }
        inflater.end();
        // Return the last index the decompressor actually operated on
        return idx - 1;
    }



    private Decompressor() { }
}
