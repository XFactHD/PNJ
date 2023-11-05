package xfacthd.pnj.impl.decoder.chunkdecoder.pixel;

import xfacthd.pnj.impl.decoder.data.DecodingImage;
import xfacthd.pnj.impl.util.Util;

final class NoInterlacePixelDecoder extends PixelDecoder
{
    private final int pixelSizeDiff;
    private int lineStart = 0;
    private int byteIndexRaw = 0;
    private int byteIndexOut = 0;

    public NoInterlacePixelDecoder(DecodingImage image, int scanlineBitDepth, int bytesPerPixelRaw, int bytesPerLineRaw)
    {
        super(image, scanlineBitDepth, bytesPerPixelRaw, bytesPerLineRaw);
        this.pixelSizeDiff = bytesPerPixelOut - bytesPerPixelRaw;
        this.scanlineSize = bytesPerLineRaw;
    }

    @Override
    protected void decodeByteOrWordDepth(byte data)
    {
        byte decoded = filter.apply(image, this, byteIndexRaw, data);
        currScanline[byteIndexRaw] = decoded;
        image.pixels()[lineStart + byteIndexOut] = decoded;

        byteIndexRaw++;
        byteIndexOut++;
        if (byteIndexRaw % bytesPerPixelRaw == 0)
        {
            byteIndexOut += pixelSizeDiff;
        }

        if (byteIndexRaw >= scanlineSize)
        {
            byteIndexRaw = 0;
            byteIndexOut = 0;
            lineStart += bytesPerLineOut;
            scanlineComplete = true;
            System.arraycopy(currScanline, 0, lastScanline, 0, scanlineSize);
        }
    }

    @Override
    protected void decodeSubByteDepth(byte data)
    {
        byte decoded = filter.apply(image, this, byteIndexRaw, data);
        currScanline[byteIndexRaw] = decoded;

        int idx = lineStart + (byteIndexRaw * pixelsPerByte * bytesPerPixelOut);
        int decodedI = Util.uint8_t(decoded);
        for (int i = pixelsPerByte - 1; i >= 0 && (idx - lineStart) < bytesPerLineOut; i--)
        {
            image.pixels()[idx] = (byte) ((decodedI >> (i * scanlineBitDepth)) & pixelMask);
            idx += bytesPerPixelOut;
        }

        byteIndexRaw++;
        if (byteIndexRaw >= scanlineSize)
        {
            byteIndexRaw = 0;
            lineStart += bytesPerLineOut;
            scanlineComplete = true;
            System.arraycopy(currScanline, 0, lastScanline, 0, scanlineSize);
        }
    }
}
