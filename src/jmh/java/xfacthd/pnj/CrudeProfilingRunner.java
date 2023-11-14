package xfacthd.pnj;

import xfacthd.pnj.api.PNJ;
import xfacthd.pnj.api.data.Image;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

public final class CrudeProfilingRunner
{
    private static final Path PATH_8BIT =  Constants.PATH_ROOT.resolve("basic/basn6a08.png");
    private static final Path PATH_16BIT = Constants.PATH_ROOT.resolve("basic/basn6a16.png");
    private static final Path PATH_MEDIUM = Constants.PATH_ROOT.resolve("custom/test_medium.png");
    private static final Path PATH_LARGE = Constants.PATH_ROOT.resolve("custom/test_large.png");
    private static final byte[] FILE_CONTENTS = BenchmarkUtil.readToByteArray(PATH_MEDIUM);
    private static final int IMG_SIZE = 64;
    private static final int BYTE_PER_PIXEL = 4;
    private static final int MULTIPLIER = 1000;
    private static final int BYTE_COUNT = IMG_SIZE * IMG_SIZE * BYTE_PER_PIXEL;
    private static final int LOOP_COUNT = BYTE_COUNT * MULTIPLIER;

    public static void main(String[] args) throws IOException
    {
        byte[] blackhole = new byte[BYTE_COUNT];
        for (int i = 0; i < LOOP_COUNT; i++)
        {
            int idx = i % BYTE_COUNT;
            Image image = PNJ.decode(new ByteArrayInputStream(FILE_CONTENTS));
            blackhole[idx] = image.pixels()[idx];
        }
        System.out.println(Arrays.toString(blackhole));
    }



    private CrudeProfilingRunner() { }
}
