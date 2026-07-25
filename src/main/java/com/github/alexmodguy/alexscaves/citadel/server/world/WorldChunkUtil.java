package com.github.alexmodguy.alexscaves.citadel.server.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public final class WorldChunkUtil {
    private WorldChunkUtil() {
    }

    public static boolean isEntityChunkLoaded(Level level, ChunkPos chunkPos) {
        return level.hasChunk(chunkPos.x, chunkPos.z);
    }

    public static boolean isEntityBlockLoaded(Level level, BlockPos pos) {
        return level.hasChunkAt(pos);
    }

    public static boolean isEntityBlockLoaded(LevelAccessor level, BlockPos pos) {
        return level instanceof Level realLevel ? isEntityBlockLoaded(realLevel, pos) : true;
    }
}
