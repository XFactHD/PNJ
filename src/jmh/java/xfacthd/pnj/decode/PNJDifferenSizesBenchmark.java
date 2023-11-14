package xfacthd.pnj.decode;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import xfacthd.pnj.BenchmarkUtil;
import xfacthd.pnj.Constants;
import xfacthd.pnj.api.PNJ;
import xfacthd.pnj.api.data.Image;
import xfacthd.pnj.api.define.ColorFormat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

@State(Scope.Benchmark)
@SuppressWarnings("MethodMayBeStatic")
public class PNJDifferenSizesBenchmark
{
    private static final Path PATH_TINY = Constants.PATH_ROOT.resolve("custom/test_tiny.png");
    private static final Path PATH_MEDIUM = Constants.PATH_ROOT.resolve("custom/test_medium.png");
    private static final Path PATH_LARGE = Constants.PATH_ROOT.resolve("custom/test_large.png");
    private static final byte[] DATA_TINY_HEAP = BenchmarkUtil.readToByteArray(PATH_TINY);
    private static final byte[] DATA_MEDIUM_HEAP = BenchmarkUtil.readToByteArray(PATH_MEDIUM);
    private static final byte[] DATA_LARGE_HEAP = BenchmarkUtil.readToByteArray(PATH_LARGE);
    private static final ByteBuffer DATA_TINY_NATIVE = BenchmarkUtil.copyToNativeBuffer(DATA_TINY_HEAP);
    private static final ByteBuffer DATA_MEDIUM_NATIVE = BenchmarkUtil.copyToNativeBuffer(DATA_MEDIUM_HEAP);
    private static final ByteBuffer DATA_LARGE_NATIVE = BenchmarkUtil.copyToNativeBuffer(DATA_LARGE_HEAP);

    @Benchmark
    public void PNJ_readTinyFile(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(PATH_TINY));
    }

    @Benchmark
    public void PNJ_readTinyBuffer(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(new ByteArrayInputStream(DATA_TINY_HEAP)));
    }

    @Benchmark
    public void STBI_readTinyFile(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load(PATH_TINY.toString(), widthBuf, heightBuf, channelsBuf, 0);
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
    public void STBI_readTinyBuffer(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load_from_memory(DATA_TINY_NATIVE, widthBuf, heightBuf, channelsBuf, 0);
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
    public void PNJ_readMediumFile(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(PATH_MEDIUM));
    }

    @Benchmark
    public void PNJ_readMediumBuffer(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(new ByteArrayInputStream(DATA_MEDIUM_HEAP)));
    }

    @Benchmark
    public void STBI_readMediumFile(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load(PATH_MEDIUM.toString(), widthBuf, heightBuf, channelsBuf, 0);
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
    public void STBI_readMediumBuffer(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load_from_memory(DATA_MEDIUM_NATIVE, widthBuf, heightBuf, channelsBuf, 0);
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
    public void PNJ_readLargeFile(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(PATH_LARGE));
    }

    @Benchmark
    public void PNJ_readLargeBuffer(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(new ByteArrayInputStream(DATA_LARGE_HEAP)));
    }

    @Benchmark
    public void STBI_readLargeFile(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load(PATH_LARGE.toString(), widthBuf, heightBuf, channelsBuf, 0);
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
    public void STBI_readLargeBuffer(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load_from_memory(DATA_LARGE_NATIVE, widthBuf, heightBuf, channelsBuf, 0);
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
        BenchmarkUtil.freeNativeBuffers(DATA_MEDIUM_NATIVE, DATA_LARGE_NATIVE);
    }
}
