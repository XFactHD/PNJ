package io.github.xfacthd.pnj;

import io.github.xfacthd.pnj.api.PNJ;
import io.github.xfacthd.pnj.api.data.Image;
import io.github.xfacthd.pnj.util.TestUtil;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;

/**
 * Test data provided by <a href="http://www.schaik.com/pngsuite/">http://www.schaik.com/pngsuite/</a>
 */
public final class Test
{
    private static int testCount = 0;
    private static boolean lastGroupErrored = false;

    public static void main(String[] args)
    {
        // Basic tests (32x32)
        testImage("basic/basn0g01"); // Grayscale          1 bit
        testImage("basic/basn0g02"); // Grayscale          2 bit
        testImage("basic/basn0g04"); // Grayscale          4 bit
        testImage("basic/basn0g08"); // Grayscale          8 bit
        testImage("basic/basn0g16"); // Grayscale         16 bit
        testImage("basic/basn2c08"); // RGB                8 bit
        testImage("basic/basn2c16"); // RGB               16 bit
        testImage("basic/basn3p01"); // Paletted           1 bit
        testImage("basic/basn3p02"); // Paletted           2 bit
        testImage("basic/basn3p04"); // Paletted           4 bit
        testImage("basic/basn3p08"); // Paletted           8 bit
        testImage("basic/basn4a08"); // Grayscale + Alpha  8 bit
        testImage("basic/basn4a16"); // Grayscale + Alpha 16 bit
        testImage("basic/basn6a08"); // RGB + Alpha        8 bit
        testImage("basic/basn6a16"); // RGB + Alpha       16 bit
        printLineIfErrored();

        // Interlacing tests (32x32)
        testImage("interlace/basi0g01"); // Grayscale          1 bit
        testImage("interlace/basi0g02"); // Grayscale          2 bit
        testImage("interlace/basi0g04"); // Grayscale          4 bit
        testImage("interlace/basi0g08"); // Grayscale          8 bit
        testImage("interlace/basi0g16"); // Grayscale         16 bit
        testImage("interlace/basi2c08"); // RGB                8 bit
        testImage("interlace/basi2c16"); // RGB               16 bit
        testImage("interlace/basi3p01"); // Paletted           1 bit
        testImage("interlace/basi3p02"); // Paletted           2 bit
        testImage("interlace/basi3p04"); // Paletted           4 bit
        testImage("interlace/basi3p08"); // Paletted           8 bit
        testImage("interlace/basi4a08"); // Grayscale + Alpha  8 bit
        testImage("interlace/basi4a16"); // Grayscale + Alpha 16 bit
        testImage("interlace/basi6a08"); // RGB + Alpha        8 bit
        testImage("interlace/basi6a16"); // RGB + Alpha       16 bit
        printLineIfErrored();

        // Odd size tests
        testImage("oddsize/s01i3p01"); //  1 x  1 Paletted 1 bit Interlaced
        testImage("oddsize/s01n3p01"); //  1 x  1 Paletted 1 bit
        testImage("oddsize/s02i3p01"); //  2 x  2 Paletted 1 bit Interlaced
        testImage("oddsize/s02n3p01"); //  2 x  2 Paletted 1 bit
        testImage("oddsize/s03i3p01"); //  3 x  3 Paletted 1 bit Interlaced
        testImage("oddsize/s03n3p01"); //  3 x  3 Paletted 1 bit
        testImage("oddsize/s04i3p01"); //  4 x  4 Paletted 1 bit Interlaced
        testImage("oddsize/s04n3p01"); //  4 x  4 Paletted 1 bit
        testImage("oddsize/s05i3p02"); //  5 x  5 Paletted 2 bit Interlaced
        testImage("oddsize/s05n3p02"); //  5 x  5 Paletted 2 bit
        testImage("oddsize/s06i3p02"); //  6 x  6 Paletted 2 bit Interlaced
        testImage("oddsize/s06n3p02"); //  6 x  6 Paletted 2 bit
        testImage("oddsize/s07i3p02"); //  7 x  7 Paletted 2 bit Interlaced
        testImage("oddsize/s07n3p02"); //  7 x  7 Paletted 2 bit
        testImage("oddsize/s08i3p02"); //  8 x  8 Paletted 2 bit Interlaced
        testImage("oddsize/s08n3p02"); //  8 x  8 Paletted 2 bit
        testImage("oddsize/s09i3p02"); //  9 x  9 Paletted 2 bit Interlaced
        testImage("oddsize/s09n3p02"); //  9 x  9 Paletted 2 bit
        testImage("oddsize/s32i3p04"); // 32 x 32 Paletted 4 bit Interlaced
        testImage("oddsize/s32n3p04"); // 32 x 32 Paletted 4 bit
        testImage("oddsize/s33i3p04"); // 33 x 33 Paletted 4 bit Interlaced
        testImage("oddsize/s33n3p04"); // 33 x 33 Paletted 4 bit
        testImage("oddsize/s34i3p04"); // 34 x 34 Paletted 4 bit Interlaced
        testImage("oddsize/s34n3p04"); // 34 x 34 Paletted 4 bit
        testImage("oddsize/s35i3p04"); // 35 x 35 Paletted 4 bit Interlaced
        testImage("oddsize/s35n3p04"); // 35 x 35 Paletted 4 bit
        testImage("oddsize/s36i3p04"); // 36 x 36 Paletted 4 bit Interlaced
        testImage("oddsize/s36n3p04"); // 36 x 36 Paletted 4 bit
        testImage("oddsize/s37i3p04"); // 37 x 37 Paletted 4 bit Interlaced
        testImage("oddsize/s37n3p04"); // 37 x 37 Paletted 4 bit
        testImage("oddsize/s38i3p04"); // 38 x 38 Paletted 4 bit Interlaced
        testImage("oddsize/s38n3p04"); // 38 x 38 Paletted 4 bit
        testImage("oddsize/s39i3p04"); // 39 x 39 Paletted 4 bit Interlaced
        testImage("oddsize/s39n3p04"); // 39 x 39 Paletted 4 bit
        testImage("oddsize/s40i3p04"); // 40 x 40 Paletted 4 bit Interlaced
        testImage("oddsize/s40n3p04"); // 40 x 40 Paletted 4 bit
        printLineIfErrored();

        // Background tests (32x32)
        testImage("background/bgai4a08"); // Grayscale + Alpha   8 bit  No BG      Interlaced
        testImage("background/bgai4a16"); // Grayscale + Alpha  16 bit  No BG      Interlaced
        testImage("background/bgan6a08"); // RGB + Alpha         8 bit  No BG
        testImage("background/bgan6a16"); // RGB + Alpha        16 bit  No BG
        testImage("background/bgbn4a08"); // Grayscale + Alpha   8 bit  Black BG
        testImage("background/bggn4a16"); // Grayscale + Alpha  16 bit  Gray BG
        testImage("background/bgwn6a08"); // RGB + Alpha         8 bit  White BG
        testImage("background/bgyn6a16"); // RGB + Alpha        16 bit  Yellow BG
        printLineIfErrored();

        // Transparency tests (32x32)
        testImage("transparency/tbbn0g04"); // Grayscale   4 bit  Alpha        Black BG
        testImage("transparency/tbbn2c16"); // RGB        16 bit  Alpha        Blue BG
        testImage("transparency/tbbn3p08"); // Paletted    8 bit  Alpha        Black BG
        testImage("transparency/tbgn2c16"); // RGB        16 bit  Alpha        Green BG
        testImage("transparency/tbgn3p08"); // Paletted    8 bit  Alpha        Light-gray BG
        testImage("transparency/tbrn2c08"); // RGB         8 bit  Alpha        Red BG
        testImage("transparency/tbwn0g16"); // Grayscale  16 bit  Alpha        White BG
        testImage("transparency/tbwn3p08"); // Paletted    8 bit  Alpha        White BG
        testImage("transparency/tbyn3p08"); // Paletted    8 bit  Alpha        Yellow BG
        testImage("transparency/tp0n0g08"); // Grayscale   8 bit  No Alpha     No BG
        testImage("transparency/tp0n2c08"); // RGB         8 bit  No Alpha     No BG
        testImage("transparency/tp0n3p08"); // Paletted    8 bit  No Alpha     No BG
        testImage("transparency/tp1n3p08"); // Paletted    8 bit  Alpha        No BG
        testImage("transparency/tm3n3p02"); // Paletted    2 bit  Multi Alpha  No BG
        printLineIfErrored();

        // Filter tests (32x32)
        testImage("filter/f00n0g08"); // Grayscale  8 bit  No filter
        testImage("filter/f00n2c08"); // RGB        8 bit  No filter
        testImage("filter/f01n0g08"); // Grayscale  8 bit  Filter 1 (SUB)
        testImage("filter/f01n2c08"); // RGB        8 bit  Filter 1 (SUB)
        testImage("filter/f02n0g08"); // Grayscale  8 bit  Filter 2 (UP)
        testImage("filter/f02n2c08"); // RGB        8 bit  Filter 2 (UP)
        testImage("filter/f03n0g08"); // Grayscale  8 bit  Filter 3 (AVG)
        testImage("filter/f03n2c08"); // RGB        8 bit  Filter 3 (AVG)
        testImage("filter/f04n0g08"); // Grayscale  8 bit  Filter 4 (PAETH)
        testImage("filter/f04n2c08"); // RGB        8 bit  Filter 4 (PAETH)
        testImage("filter/f99n0g04"); // Grayscale  4 bit  One filter per scanline
        printLineIfErrored();

        // Chunk order tests (32x32)
        testImage("order/oi1n0g16"); // Grayscale  16 bit  Single IDAT
        testImage("order/oi1n2c16"); // RGB        16 bit  Single IDAT
        testImage("order/oi2n0g16"); // Grayscale  16 bit  Two IDAT
        testImage("order/oi2n2c16"); // RGB        16 bit  Two IDAT
        testImage("order/oi4n0g16"); // Grayscale  16 bit  Four unequally sized IDAT
        testImage("order/oi4n2c16"); // RGB        16 bit  Four unequally sized IDAT
        testImage("order/oi9n0g16"); // Grayscale  16 bit  One-length IDATs
        testImage("order/oi9n2c16"); // RGB        16 bit  One-length IDATs
        printLineIfErrored();

        // Compression tests (32x32)
        testImage("compression/z00n2c08"); // RGB  8 bit  Level 0 (none)
        testImage("compression/z03n2c08"); // RGB  8 bit  Level 3
        testImage("compression/z06n2c08"); // RGB  8 bit  Level 6 (default)
        testImage("compression/z09n2c08"); // RGB  8 bit  Level 9 (max)
        printLineIfErrored();

        // Corruption tests
        testImageError("corruption/xs1n0g01"); // signature byte 1 MSBit reset to zero
        testImageError("corruption/xs2n0g01"); // signature byte 2 is a 'Q'
        testImageError("corruption/xs4n0g01"); // signature byte 4 lowercase
        testImageError("corruption/xs7n0g01"); // 7th byte a space instead of control-Z
        testImageError("corruption/xcrn0g04"); // added cr bytes
        testImageError("corruption/xlfn0g04"); // added lf bytes
        testImageError("corruption/xhdn0g08"); // incorrect IHDR checksum
        testImageError("corruption/xc1n0g08"); // color format 1
        testImageError("corruption/xc9n2c08"); // color format 9
        testImageError("corruption/xd0n2c08"); // bit-depth 0
        testImageError("corruption/xd3n2c08"); // bit-depth 3
        testImageError("corruption/xd9n2c08"); // bit-depth 99
        testImageError("corruption/xdtn0g01"); // missing IDAT chunk
        testImageError("corruption/xcsn0g01"); // incorrect IDAT checksum
        printLineIfErrored();

        // Custom tests
        testImage("custom/test_tiny");
        testImage("custom/test_large");
        printLineIfErrored();

        // External tests (unexpectedly broken)
        //testImage("external/default"); // Incorrectly placed iCCP chunk
        //testImage("external/paper"); // Incorrectly placed iCCP chunk
        testEncodeOnly("custom/minimap_0_0"); // Causes deflater to request input in final pass despite not needing it
        printLineIfErrored();

        System.out.println("Tested " + testCount + " images");
    }

    private static void testImage(String fileName)
    {
        Path base = Path.of("./testdata/");
        Path source = base.resolve(fileName + ".png");
        Path outPpm = base.resolve(fileName + "_out.ppm");
        Path outPng = base.resolve(fileName + "_out.png");
        String state = "";

        try
        {
            state = "initial decode";
            Image image = PNJ.decode(source);
            state = "write to ppm";
            TestUtil.writeToPPM(outPpm, image);
            state = "encode";
            PNJ.encode(outPng, image);
            state = "cross-check decode";
            PNJ.decode(outPng);

            state = "ref compare";
            compareAgainstReferenceLoader(image, source, fileName);
        }
        catch (Throwable t)
        {
            System.err.printf("%s: %s failed with %s\n".formatted(fileName, state, TestUtil.throwableToString(t)));
            lastGroupErrored = true;

            try
            {
                Files.deleteIfExists(outPpm);
                Files.deleteIfExists(outPng);
            }
            catch (IOException ignored) { }
        }

        testCount++;
    }

    private static void testEncodeOnly(String fileName)
    {
        Path base = Path.of("./testdata/");
        Path source = base.resolve(fileName + ".ppm");
        Path outPng = base.resolve(fileName + "_out.png");
        String state = "";

        try
        {
            state = "read from ppm";
            Image image = TestUtil.readFromPPM(source);
            state = "encode";
            PNJ.encode(outPng, image);
            state = "cross-check decode";
            PNJ.decode(outPng);
        }
        catch (Throwable t)
        {
            System.err.printf("%s: %s failed with %s\n".formatted(fileName, state, TestUtil.throwableToString(t)));
            lastGroupErrored = true;

            try
            {
                Files.deleteIfExists(outPng);
            }
            catch (IOException ignored) { }
        }

        testCount++;
    }

    private static void compareAgainstReferenceLoader(Image image, Path source, String fileName)
    {
        Image expected = TestUtil.loadComparisonImage(source, image.sampleDepth());
        if (expected == null) return;

        int mismatch = Arrays.mismatch(image.pixels(), expected.pixels());
        if (mismatch == -1) return;

        int bytesPerElem = Math.max(image.sampleDepth() / 8, 1);
        int bytesPerPixel = image.colorFormat().getBytePerPixel(bytesPerElem, false);
        int pixelStart = mismatch - (mismatch % bytesPerPixel);
        System.err.printf(
                "%s: result doesn't match expected at index %d (pixel %d). Expected: %s, Got: %s\n",
                fileName,
                mismatch,
                pixelStart,
                TestUtil.rangeToString(expected.pixels(), pixelStart, bytesPerPixel, bytesPerElem),
                TestUtil.rangeToString(image.pixels(), pixelStart, bytesPerPixel, bytesPerElem)
        );
        lastGroupErrored = true;
    }

    private static void testImageError(String fileName)
    {
        boolean errored = false;
        try
        {
            PNJ.decode(Path.of("./testdata/" + fileName + ".png"));
        }
        catch (Throwable t)
        {
            System.out.println(fileName + ": " + TestUtil.throwableToString(t));
            errored = true;
        }
        if (!errored)
        {
            System.err.println(fileName + ": no error encountered");
        }

        testCount++;
    }

    private static void printLineIfErrored()
    {
        if (lastGroupErrored)
        {
            lastGroupErrored = false;
            System.err.println();
        }
    }



    private Test() { }
}
