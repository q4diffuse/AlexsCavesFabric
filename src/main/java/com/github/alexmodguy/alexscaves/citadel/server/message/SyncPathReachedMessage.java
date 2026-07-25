package com.github.alexmodguy.alexscaves.citadel.server.message;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class SyncPathReachedMessage implements CustomPacketPayload {
    public static final Type<SyncPathReachedMessage> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath("alexscaves", "citadel_sync_path_reached"));

    private final Set<BlockPos> reached;

    public SyncPathReachedMessage(Set<BlockPos> reached) {
        this.reached = reached;
    }

    public Set<BlockPos> getReached() {
        return reached;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
