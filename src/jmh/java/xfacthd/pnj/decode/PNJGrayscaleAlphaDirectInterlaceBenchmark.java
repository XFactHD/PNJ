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
public class PNJGrayscaleAlphaDirectInterlaceBenchmark
{
    private static final Path PATH_8BIT =  Constants.PATH_ROOT.resolve("interlace/basi4a08.png");
    private static final Path PATH_16BIT = Constants.PATH_ROOT.resolve("interlace/basi4a16.png");
    private static final byte[] DATA_8BIT_HEAP = BenchmarkUtil.readToByteArray(PATH_8BIT);
    private static final byte[] DATA_16BIT_HEAP = BenchmarkUtil.readToByteArray(PATH_16BIT);
    private static final ByteBuffer DATA_8BIT_NATIVE = BenchmarkUtil.copyToNativeBuffer(DATA_8BIT_HEAP);
    private static final ByteBuffer DATA_16BIT_NATIVE = BenchmarkUtil.copyToNativeBuffer(DATA_16BIT_HEAP);

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



    @Benchmark
    public void PNJ_read16bitFile(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(PATH_16BIT));
    }

    @Benchmark
    public void PNJ_read16bitBuffer(Blackhole bh) throws IOException
    {
        bh.consume(PNJ.decode(new ByteArrayInputStream(DATA_16BIT_HEAP)));
    }

    @Benchmark
    public void STBI_read16bitFile(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load(PATH_16BIT.toString(), widthBuf, heightBuf, channelsBuf, 0);
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
    public void STBI_read16bitBuffer(Blackhole bh)
    {
        try (MemoryStack stack = MemoryStack.stackPush())
        {
            IntBuffer widthBuf = stack.mallocInt(1);
            IntBuffer heightBuf = stack.mallocInt(1);
            IntBuffer channelsBuf = stack.mallocInt(1);
            ByteBuffer pixBuf = STBImage.stbi_load_from_memory(DATA_16BIT_NATIVE, widthBuf, heightBuf, channelsBuf, 0);
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
        BenchmarkUtil.freeNativeBuffers(DATA_8BIT_NATIVE, DATA_16BIT_NATIVE);
    }
}
