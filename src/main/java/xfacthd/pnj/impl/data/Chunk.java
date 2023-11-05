package xfacthd.pnj.impl.data;

import xfacthd.pnj.impl.define.ChunkType;

public record Chunk(ChunkType type, byte[] data)
{

}
