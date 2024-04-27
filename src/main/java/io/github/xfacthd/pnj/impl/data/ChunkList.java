package io.github.xfacthd.pnj.impl.data;

import io.github.xfacthd.pnj.impl.define.ChunkType;

public final class ChunkList
{
    private Chunk[] chunks = new Chunk[8];
    private int chunkCount = 0;
    private long encounteredTypes = 0;

    public void add(ChunkType type, byte[] chunkData)
    {
        markType(type);

        // Immediately discard ignored chunks, no point in keeping them in memory,
        // but mark them as seen for ordering checks
        if (type.isSupported())
        {
            if (chunkCount >= chunks.length)
            {
                Chunk[] newChunks = new Chunk[chunkCount * 2];
                System.arraycopy(chunks, 0, newChunks, 0, chunkCount);
                chunks = newChunks;
            }
            chunks[chunkCount] = new Chunk(type, chunkData);
            chunkCount++;
        }
    }

    public void markType(ChunkType type)
    {
        encounteredTypes |= (1L << type.ordinal());
    }

    public Chunk get(int idx)
    {
        return chunks[idx];
    }

    public boolean isEmpty()
    {
        return chunkCount == 0;
    }

    public int size()
    {
        return chunkCount;
    }

    public boolean containsType(ChunkType type)
    {
        return (encounteredTypes & (1L << type.ordinal())) != 0;
    }

    public ChunkType getLastType()
    {
        return chunkCount == 0 ? null : chunks[chunkCount - 1].type();
    }
}
