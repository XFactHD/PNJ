package xfacthd.pnj.api.define;

/**
 * Configuration options to modify the behaviour of the PNG decoder
 */
public enum DecoderOption
{
    /** If specified, additional transparency data from the tRNS chunk will be ignored */
    IGNORE_TRANSPARENCY,
    /** If specified, the background color from the bKGD chunk will be applied to the decoded image */
    APPLY_BACKGROUND,
}
