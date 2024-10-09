package io.github.xfacthd.pnj.api.define;

/**
 * Configuration options to modify the behaviour of the PNG encoder
 */
public enum EncoderOption
{
    /**
     * If specified, Deflate compression is disabled, increasing speed at the cost of increased file size.
     * <p>
     * Overruled by both {@link #FAST_COMPRESSION} and {@link #BEST_COMPRESSION} if specified together.
     * <p>
     * If neither this nor {@link #FAST_COMPRESSION} or {@link #BEST_COMPRESSION} is specified, then the default compression level is used.
     */
    NO_COMPRESSION,
    /**
     * If specified, the fastest Deflate compression level will be used, increasing speed at the cost of increased file size.
     * <p>
     * Overrules {@link #NO_COMPRESSION} and is overruled by {@link #BEST_COMPRESSION} if specified together.
     * <p>
     * If neither this nor {@link #NO_COMPRESSION} or {@link #BEST_COMPRESSION} is specified, then the default compression level is used.
     */
    FAST_COMPRESSION,
    /**
     * If specified, the best Deflate compression level will be used, reducing file size at the cost of speed.
     * <p>
     * Overrules both {@link #NO_COMPRESSION} and {@link #FAST_COMPRESSION} if specified together.
     * <p>
     * If neither this nor {@link #NO_COMPRESSION} or {@link #FAST_COMPRESSION} is specified, then the default compression level is used.
     */
    BEST_COMPRESSION,
    /**
     * If specified, the encoder will not attempt to palettize the image, increasing speed at the cost of potentially increased file size.
     */
    DISABLE_PALETTE_EXTRACT,
    /**
     * If specified and the image has not been palettized, the encoder will not attempt to extract the alpha channel into the separate
     * tRNS chunk if the image contains an alpha channel, increasing speed at the cost of potentially increased file size.
     */
    DISABLE_ALPHA_EXTRACT,
}
