package io.github.xfacthd.pnj;

import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class BenchmarkUtil
{
    public static byte[] readToByteArray(Path path)
    {
        try
        {
            return Files.readAllBytes(path);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to read file at " + path + " for benchmark", e);
        }
    }

    public static ByteBuffer copyToNativeBuffer(byte[] data)
    {
        ByteBuffer buffer = MemoryUtil.memAlloc(data.length);
        buffer.put(0, data);
        return buffer;
    }

    public static void freeNativeBuffers(ByteBuffer... buffers)
    {
        for (ByteBuffer buffer : buffers)
        {
            MemoryUtil.memFree(buffer);
        }
    }



    private BenchmarkUtil() { }
}
