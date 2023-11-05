package xfacthd.pnj.impl.data;

import xfacthd.pnj.impl.define.ChunkType;

import java.util.ArrayList;
import java.util.List;

public final class ChunkList
{
    private final List<Chunk> chunks = new ArrayList<>();
    private long encounteredTypes = 0;

    public void add(Chunk chunk)
    {
        markType(chunk.type());
        chunks.add(chunk);
    }

    public void markType(ChunkType type)
    {
        encounteredTypes |= (1L << type.ordinal());
    }

    public Chunk get(int idx)
    {
        return chunks.get(idx);
    }

    public boolean isEmpty()
    {
        return chunks.isEmpty();
    }

    public int size()
    {
        return chunks.size();
    }

    public boolean containsType(ChunkType type)
    {
        return (encounteredTypes & (1L << type.ordinal())) != 0;
    }

    public ChunkType getLastType()
    {
        if (chunks.isEmpty())
        {
            return null;
        }
        return chunks.get(chunks.size() - 1).type();
    }

    public int firstIndexOfType(ChunkType type)
    {
        if (containsType(type))
        {
            for (int i = 0; i < chunks.size(); i++)
            {
                if (chunks.get(i).type() == type)
                {
                    return i;
                }
            }
        }
        return -1;
    }
}
