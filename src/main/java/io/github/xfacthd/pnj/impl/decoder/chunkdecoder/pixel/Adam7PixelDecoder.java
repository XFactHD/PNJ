package io.github.xfacthd.pnj.impl.decoder.chunkdecoder.pixel;

import io.github.xfacthd.pnj.api.define.ColorFormat;
import io.github.xfacthd.pnj.impl.decoder.data.DecodingImage;
import io.github.xfacthd.pnj.impl.util.Util;

import java.util.Arrays;

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
        byte decoded = filter.apply(this, colScan, data);
        storePixelData(colScan, decoded);
        int idx = row * bytesPerLineOut + (col * bytesPerPixelOut) + (colScan % bytesPerPixelRaw);
        pixels[idx] = decoded;

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
        byte decoded = filter.apply(this, captureIdx, data);
        storePixelData(captureIdx, decoded);

        int decodedI = Util.uint8_t(decoded);
        for (int i = pixelsPerByte - 1; i >= 0; i--)
        {
            int idx = row * bytesPerLineOut + (col * bytesPerPixelOut) + (colScan % bytesPerPixelRaw);
            pixels[idx] = (byte) ((decodedI >> (i * scanlineBitDepth)) & pixelMask);

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
        advanceScanlineBuffer();
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
            Arrays.fill(scanlineBuffer, (byte) 0);
        }

        return false;
    }

    private boolean isEmptyPass()
    {
        return STARTING_ROW[pass] >= height || STARTING_COL[pass] >= width;
    }

    private int calculateScanlineSize(int pass)
    {
        int pixels = (width - STARTING_COL[pass]) / COL_INCREMENT[pass] + 1;
        return Math.min(Util.getBytesPerLine(pixels, scanlineBitDepth, elemCount), bytesPerLineRaw);
    }
}
