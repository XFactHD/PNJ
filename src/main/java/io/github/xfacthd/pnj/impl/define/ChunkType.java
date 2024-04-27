package io.github.xfacthd.pnj.impl.define;

import io.github.xfacthd.pnj.impl.data.ChunkList;

import java.util.Arrays;
import java.util.function.Predicate;

public enum ChunkType
{
    IHDR(new byte[] { 'I', 'H', 'D', 'R' }, false,  true, ChunkList::isEmpty),
    PLTE(new byte[] { 'P', 'L', 'T', 'E' }, false,  true, list -> true)
    {
        @Override
        public boolean isValidPosition(ChunkList list)
        {
            // Forward reference prevents use of lambda
            return super.isValidPosition(list) && !list.containsType(IDAT);
        }
    },
    IDAT(new byte[] { 'I', 'D', 'A', 'T' },  true,  true, list -> true)
    {
        @Override
        public boolean isValidPosition(ChunkList list)
        {
            // Forward reference prevents use of lambda
            return super.isValidPosition(list) && (!list.containsType(IDAT) || list.getLastType() == IDAT);
        }
    },
    IEND(new byte[] { 'I', 'E', 'N', 'D' }, false,  true, list -> !list.isEmpty()),

    cHRM(new byte[] { 'c', 'H', 'R', 'M' }, false, false, list -> !list.containsType(PLTE) && !list.containsType(IDAT)),
    gAMA(new byte[] { 'g', 'A', 'M', 'A' }, false, false, list -> !list.containsType(PLTE) && !list.containsType(IDAT)),
    iCCP(new byte[] { 'i', 'C', 'C', 'P' }, false, false, list -> !list.containsType(PLTE) && !list.containsType(IDAT)),
    sBIT(new byte[] { 's', 'B', 'I', 'T' }, false, false, list -> !list.containsType(PLTE) && !list.containsType(IDAT)),
    sRGB(new byte[] { 's', 'R', 'G', 'B' }, false, false, list -> !list.containsType(PLTE) && !list.containsType(IDAT)),
    bKGD(new byte[] { 'b', 'K', 'G', 'D' }, false,  true, list -> /*list.containsType(PLTE) && */!list.containsType(IDAT)),
    hIST(new byte[] { 'h', 'I', 'S', 'T' }, false, false, list -> /*list.containsType(PLTE) && */!list.containsType(IDAT)),
    tRNS(new byte[] { 't', 'R', 'N', 'S' }, false,  true, list -> /*list.containsType(PLTE) && */!list.containsType(IDAT)),
    pHYs(new byte[] { 'p', 'H', 'Y', 's' }, false, false, list -> !list.containsType(IDAT)),
    sPLT(new byte[] { 's', 'P', 'L', 'T' },  true, false, list -> !list.containsType(IDAT)),
    tIME(new byte[] { 't', 'I', 'M', 'E' }, false, false, list -> true),
    iTXt(new byte[] { 'i', 'T', 'X', 't' },  true, false, list -> true),
    tEXt(new byte[] { 't', 'E', 'X', 't' },  true, false, list -> true),
    zTXt(new byte[] { 'z', 'T', 'X', 't' },  true, false, list -> true),
    ;

    private static final ChunkType[] TYPES = values();

    private final String name = toString();
    private final byte[] typeCode;
    private final boolean mandatory;
    private final boolean registered;
    private final boolean reservedFlag;
    private final boolean safeToCopy;
    private final boolean multiple;
    private final boolean supported;
    private final Predicate<ChunkList> orderingConstraint;

    ChunkType(byte[] typeCode, boolean multiple, boolean supported, Predicate<ChunkList> orderingConstraint)
    {
        this.typeCode = typeCode;
        this.mandatory = isMandatory(typeCode);
        this.registered = isPublicType(typeCode);
        this.reservedFlag = isReservedFlagSet(typeCode);
        this.safeToCopy = isSafeToCopy(typeCode);
        this.multiple = multiple;
        this.supported = supported;
        this.orderingConstraint = orderingConstraint;
    }

    public String getName()
    {
        return name;
    }

    public byte[] getTypeCode()
    {
        return typeCode;
    }

    public boolean isMandatory()
    {
        return mandatory;
    }

    public boolean isPublicType()
    {
        return registered;
    }

    public boolean isReservedFlagSet()
    {
        return reservedFlag;
    }

    public boolean isSafeToCopy()
    {
        return safeToCopy;
    }

    public boolean areMultipleAllowed()
    {
        return multiple;
    }

    public boolean isSupported()
    {
        return supported;
    }

    public boolean isValidPosition(ChunkList list)
    {
        if (!multiple && list.containsType(this))
        {
            return false;
        }
        return !list.containsType(IEND) && orderingConstraint.test(list);
    }



    public static ChunkType decode(byte[] typeCode)
    {
        for (ChunkType type : TYPES)
        {
            if (Arrays.equals(typeCode, type.typeCode))
            {
                return type;
            }
        }
        if (isMandatory(typeCode))
        {
            throw new IllegalArgumentException("Unknown mandatory chunk type: " + new String(typeCode));
        }
        //System.err.println("Unknown chunk type: " + new String(typeCode));
        return null;
    }

    public static boolean isMandatory(byte[] typeCode)
    {
        return (typeCode[0] & 0b00100000) == 0;
    }

    public static boolean isPublicType(byte[] typeCode)
    {
        return (typeCode[1] & 0b00100000) == 0;
    }

    public static boolean isReservedFlagSet(byte[] typeCode)
    {
        return (typeCode[2] & 0b00100000) != 0;
    }

    public static boolean isSafeToCopy(byte[] typeCode)
    {
        return (typeCode[3] & 0b00100000) != 0;
    }
}
