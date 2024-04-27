package io.github.xfacthd.pnj.impl.data;

import io.github.xfacthd.pnj.impl.define.ChunkType;

public record Chunk(ChunkType type, byte[] data)
{

}
