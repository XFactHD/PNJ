package io.github.xfacthd.pnj.decode;

import io.github.xfacthd.pnj.BenchmarkUtil;
import io.github.xfacthd.pnj.Constants;
import io.github.xfacthd.pnj.api.PNJ;
import io.github.xfacthd.pnj.api.data.Image;
import io.github.xfacthd.pnj.api.define.ColorFormat;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

@State(Scope.Benchmark)
@SuppressWarnings("MethodMayBeStatic")
public class PNJPaletteProgressiveBenchmark
{
    private static final Path PATH_1BIT =  Constants.PATH_ROOT.resolve("basic/basn3p01.png");
    private static final Path PATH_2BIT =  Constants.PATH_ROOT.resolve("basic/basn3p02.png");
    private static final Path PATH_4BIT =  Constants.PATH_ROOT.resolve("basic/basn3p04.png");
    private static final Path PATH_8BIT =  Constants.PATH_ROOT.resolve("basic/basn3p08.png");
    private static final byte[] DATA_1BIT_HEAP = BenchmarkUtil.readToByteArray(PATH_1BIT);
    private static final byte[] DATA_2BIT_HEAP = BenchmarkUtil.readToByteArray(PATH_2BIT);
    private static final byte[] DATA_4BIT_HEAP = BenchmarkUtil.readToByteArray(PATH_4BIT);
    private static final byte[] DATA_8BIT_HEAP = BenchmarkUtil.readToByteArray(PATH_8BIT);
    private static final ByteBuffer DATA_1BIT_NATIVE = BenchmarkUtil.copyToNativeBuffer(DATA_1BIT_HEAP);
    private static final ByteBuffer DATA_2BIT_NATIVE = BenchmarkUtil.copyToNativeBuffer(DATA_2BIT_HEAP);
    private static final ByteBuffer DATA_4BIT_NATIVE = BenchmarkUtil.copyToNativeBuffer(DATA_4BIT_HEAP);
    private static final ByteBuffer DATA_8BIT_NATIVE = BenchmarkUtil.copyToNativeBuffer(DATA_8BIT_HEAP);

    @Benchmark
    public void PNJ_read1bitFile(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(PATH_1BIT));
    }

    @Benchmark
    public void PNJ_read1bitBuffer(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(new ByteArrayInputStream(DATA_1BIT_HEAP)));
    }

    @Benchmark
    public void STBI_read1bitFile(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load(PATH_1BIT.toString(), widthBuf, heightBuf, channelsBuf, 0);
            if (pixBuf == null) throw new IllegalStateException("STBI decode failed");
            byte[] pixels = new byte[pixBuf.limit()];
            pixBuf.get(0, pixels);
            ColorFormat format = switch (channelsBuf.get())
            {
                case 1 -> ColorFormat.GRAYSCALE;
                case 2 -> ColorFormat.GRAYSCALE_ALPHA;
                case 3 -> ColorFormat.RGB;
                case 4 -> ColorFormat.RGB_ALPHA;
                default -> throw new IllegalStateException("Invalid channel count");
            };
            bh.consume(new Image(widthBuf.get(), heightBuf.get(), format, 8, pixels));
            STBImage.stbi_image_free(pixBuf);
        }
    }

    @Benchmark
    public void STBI_read1bitBuffer(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load_from_memory(DATA_1BIT_NATIVE, widthBuf, heightBuf, channelsBuf, 0);
            if (pixBuf == null) throw new IllegalStateException("STBI decode failed");
            byte[] pixels = new byte[pixBuf.limit()];
            pixBuf.get(0, pixels);
            ColorFormat format = switch (channelsBuf.get())
            {
                case 1 -> ColorFormat.GRAYSCALE;
                case 2 -> ColorFormat.GRAYSCALE_ALPHA;
                case 3 -> ColorFormat.RGB;
                case 4 -> ColorFormat.RGB_ALPHA;
                default -> throw new IllegalStateException("Invalid channel count");
            };
            bh.consume(new Image(widthBuf.get(), heightBuf.get(), format, 8, pixels));
            STBImage.stbi_image_free(pixBuf);
        }
    }



    @Benchmark
    public void PNJ_read2bitFile(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(PATH_2BIT));
    }

    @Benchmark
    public void PNJ_read2bitBuffer(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(new ByteArrayInputStream(DATA_2BIT_HEAP)));
    }

    @Benchmark
    public void STBI_read2bitFile(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load(PATH_2BIT.toString(), widthBuf, heightBuf, channelsBuf, 0);
            if (pixBuf == null) throw new IllegalStateException("STBI decode failed");
            byte[] pixels = new byte[pixBuf.limit()];
            pixBuf.get(0, pixels);
            ColorFormat format = switch (channelsBuf.get())
            {
                case 1 -> ColorFormat.GRAYSCALE;
                case 2 -> ColorFormat.GRAYSCALE_ALPHA;
                case 3 -> ColorFormat.RGB;
                case 4 -> ColorFormat.RGB_ALPHA;
                default -> throw new IllegalStateException("Invalid channel count");
            };
            bh.consume(new Image(widthBuf.get(), heightBuf.get(), format, 8, pixels));
            STBImage.stbi_image_free(pixBuf);
        }
    }

    @Benchmark
    public void STBI_read2bitBuffer(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load_from_memory(DATA_2BIT_NATIVE, widthBuf, heightBuf, channelsBuf, 0);
            if (pixBuf == null) throw new IllegalStateException("STBI decode failed");
            byte[] pixels = new byte[pixBuf.limit()];
            pixBuf.get(0, pixels);
            ColorFormat format = switch (channelsBuf.get())
            {
                case 1 -> ColorFormat.GRAYSCALE;
                case 2 -> ColorFormat.GRAYSCALE_ALPHA;
                case 3 -> ColorFormat.RGB;
                case 4 -> ColorFormat.RGB_ALPHA;
                default -> throw new IllegalStateException("Invalid channel count");
            };
            bh.consume(new Image(widthBuf.get(), heightBuf.get(), format, 8, pixels));
            STBImage.stbi_image_free(pixBuf);
        }
    }



    @Benchmark
    public void PNJ_read4bitFile(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(PATH_4BIT));
    }

    @Benchmark
    public void PNJ_read4bitBuffer(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(new ByteArrayInputStream(DATA_4BIT_HEAP)));
    }

    @Benchmark
    public void STBI_read4bitFile(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load(PATH_4BIT.toString(), widthBuf, heightBuf, channelsBuf, 0);
            if (pixBuf == null) throw new IllegalStateException("STBI decode failed");
            byte[] pixels = new byte[pixBuf.limit()];
            pixBuf.get(0, pixels);
            ColorFormat format = switch (channelsBuf.get())
            {
                case 1 -> ColorFormat.GRAYSCALE;
                case 2 -> ColorFormat.GRAYSCALE_ALPHA;
                case 3 -> ColorFormat.RGB;
                case 4 -> ColorFormat.RGB_ALPHA;
                default -> throw new IllegalStateException("Invalid channel count");
            };
            bh.consume(new Image(widthBuf.get(), heightBuf.get(), format, 8, pixels));
            STBImage.stbi_image_free(pixBuf);
        }
    }

    @Benchmark
    public void STBI_read4bitBuffer(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load_from_memory(DATA_4BIT_NATIVE, widthBuf, heightBuf, channelsBuf, 0);
            if (pixBuf == null) throw new IllegalStateException("STBI decode failed");
            byte[] pixels = new byte[pixBuf.limit()];
            pixBuf.get(0, pixels);
            ColorFormat format = switch (channelsBuf.get())
            {
                case 1 -> ColorFormat.GRAYSCALE;
                case 2 -> ColorFormat.GRAYSCALE_ALPHA;
                case 3 -> ColorFormat.RGB;
                case 4 -> ColorFormat.RGB_ALPHA;
                default -> throw new IllegalStateException("Invalid channel count");
            };
            bh.consume(new Image(widthBuf.get(), heightBuf.get(), format, 8, pixels));
            STBImage.stbi_image_free(pixBuf);
        }
    }



    @Benchmark
    public void PNJ_read8bitFile(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(PATH_8BIT));
    }

    @Benchmark
    public void PNJ_read8bitBuffer(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(new ByteArrayInputStream(DATA_8BIT_HEAP)));
    }

    @Benchmark
    public void STBI_read8bitFile(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load(PATH_8BIT.toString(), widthBuf, heightBuf, channelsBuf, 0);
            if (pixBuf == null) throw new IllegalStateException("STBI decode failed");
            byte[] pixels = new byte[pixBuf.limit()];
            pixBuf.get(0, pixels);
            ColorFormat format = switch (channelsBuf.get())
            {
                case 1 -> ColorFormat.GRAYSCALE;
                case 2 -> ColorFormat.GRAYSCALE_ALPHA;
                case 3 -> ColorFormat.RGB;
                case 4 -> ColorFormat.RGB_ALPHA;
                default -> throw new IllegalStateException("Invalid channel count");
            };
            bh.consume(new Image(widthBuf.get(), heightBuf.get(), format, 8, pixels));
            STBImage.stbi_image_free(pixBuf);
        }
    }

    @Benchmark
    public void STBI_read8bitBuffer(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load_from_memory(DATA_8BIT_NATIVE, widthBuf, heightBuf, channelsBuf, 0);
            if (pixBuf == null) throw new IllegalStateException("STBI decode failed");
            byte[] pixels = new byte[pixBuf.limit()];
            pixBuf.get(0, pixels);
            ColorFormat format = switch (channelsBuf.get())
            {
                case 1 -> ColorFormat.GRAYSCALE;
                case 2 -> ColorFormat.GRAYSCALE_ALPHA;
                case 3 -> ColorFormat.RGB;
                case 4 -> ColorFormat.RGB_ALPHA;
                default -> throw new IllegalStateException("Invalid channel count");
            };
            bh.consume(new Image(widthBuf.get(), heightBuf.get(), format, 8, pixels));
            STBImage.stbi_image_free(pixBuf);
        }
    }



    @TearDown(Level.Trial)
    public void onBenchmarkEnd()
    {
        BenchmarkUtil.freeNativeBuffers(DATA_1BIT_NATIVE, DATA_2BIT_NATIVE, DATA_4BIT_NATIVE, DATA_8BIT_NATIVE);
    }
}
