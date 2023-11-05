package xfacthd.pnj.impl.decoder.chunkdecoder.pixel;

import xfacthd.pnj.api.define.ColorFormat;
import xfacthd.pnj.impl.decoder.data.DecodingImage;
import xfacthd.pnj.impl.util.Util;

import java.util.Arrays;

//FIXME: formats with more than one byte per pixel produce artifacts
final class Adam7PixelDecoder extends PixelDecoder
{
    private static final int[] STARTING_ROW = new int[] { 0, 0, 4, 0, 2, 0, 1 };
    private static final int[] STARTING_COL = new int[] { 0, 4, 0, 2, 0, 1, 0 };
    private static final int[] ROW_INCREMENT = new int[] { 8, 8, 8, 4, 4, 2, 2 };
    private static final int[] COL_INCREMENT = new int[] { 8, 8, 4, 4, 2, 2, 1 };

    private final int elemCount;
    private int pass = 0;
    private int row = 0;
    private int col = 0;
    private int colScan = 0;

    public Adam7PixelDecoder(DecodingImage image, int scanlineBitDepth, int bytesPerPixelRaw, int bytesPerLineRaw)
    {
        super(image, scanlineBitDepth, bytesPerPixelRaw, bytesPerLineRaw);
        ColorFormat colorFormat = image.colorFormat();
        this.elemCount = colorFormat.isPaletteUsed() ? 1 : colorFormat.getElementCount();
        this.scanlineSize = calculateScanlineSize(0);
    }

    @Override
    protected void decodeByteOrWordDepth(byte data)
    {
        byte decoded = filter.apply(image, this, colScan, data);
        currScanline[colScan] = decoded;
        int idx = row * bytesPerLineOut + (col * bytesPerPixelOut) + (colScan % bytesPerPixelRaw);
        image.pixels()[idx] = decoded;

        colScan++;
        if ((colScan % bytesPerPixelRaw == 0))
        {
            advance();
        }
    }

    @Override
    protected void decodeSubByteDepth(byte data)
    {
        int captureIdx = colScan / pixelsPerByte;
        byte decoded = filter.apply(image, this, captureIdx, data);
        currScanline[captureIdx] = decoded;

        int decodedI = Util.uint8_t(decoded);
        for (int i = pixelsPerByte - 1; i >= 0; i--)
        {
            int idx = row * bytesPerLineOut + (col * bytesPerPixelOut) + (colScan % bytesPerPixelRaw);
            image.pixels()[idx] = (byte) ((decodedI >> (i * scanlineBitDepth)) & pixelMask);

            colScan++;
            if (!advance())
            {
                break;
            }
        }
    }

    // Returns true as long as the row doesn't change
    private boolean advance()
    {
        col += COL_INCREMENT[pass];
        if (col < width)
        {
            return true;
        }

        scanlineComplete = true;
        System.arraycopy(currScanline, 0, lastScanline, 0, scanlineSize);
        row += ROW_INCREMENT[pass];
        col = STARTING_COL[pass];
        colScan = 0;
        if (row < height)
        {
            return false;
        }

        do
        {
            pass++;
        }
        while (pass < 7 && isEmptyPass());

        if (pass < 7)
        {
            scanlineSize = calculateScanlineSize(pass);
            row = STARTING_ROW[pass];
            col = STARTING_COL[pass];
            Arrays.fill(lastScanline, (byte) 0);
            Arrays.fill(currScanline, (byte) 0);
        }

        return false;
    }

    private boolean isEmptyPass()
    {
        return STARTING_ROW[pass] >= height || STARTING_COL[pass] >= width;
    }

    private int calculateScanlineSize(int pass)
    {
        int pixels = (width - STARTING_COL[pass]) / COL_INCREMENT[pass];
        return Util.getBytesPerLine(pixels, scanlineBitDepth, elemCount);
    }
}
