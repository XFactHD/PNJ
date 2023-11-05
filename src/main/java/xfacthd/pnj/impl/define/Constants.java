package xfacthd.pnj.impl.define;

import xfacthd.pnj.impl.data.chunk.*;

public final class Constants
{
    public static final byte[] PNG_MAGIC = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };

    public static final class Header
    {
        public static final int LENGTH = 4 + 4 + 1 + 1 + 1 + 1 + 1;
        public static final int OFFSET_WIDTH = 0;
        public static final int OFFSET_HEIGHT = 4;
        public static final int OFFSET_BIT_DEPTH = 8;
        public static final int OFFSET_COLOR_TYPE = 9;
        public static final int OFFSET_COMPRESSION_METHOD = 10;
        public static final int OFFSET_FILTER_METHOD = 11;
        public static final int OFFSET_INTERLACE_METHOD = 12;



        private Header() { }
    }

    public static final class Data
    {
        public static final DataKey<Palette> KEY_PALETTE = new DataKey<>();
        public static final DataKey<Transparency> KEY_TRANSPARENCY = new DataKey<>();
        public static final DataKey<BackgroundColor> KEY_BACKGROUND_COLOR = new DataKey<>();



        private Data() { }
    }



    private Constants() { }
}
