package com.github.alexthe666.citadel.server.message;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncClientTickRateMessage(CompoundTag compound) implements CustomPacketPayload {
    public static final Type<SyncClientTickRateMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("citadel", "tick_rate"));
    public static final StreamCodec<FriendlyByteBuf, SyncClientTickRateMessage> CODEC = StreamCodec.ofMember(SyncClientTickRateMessage::write, SyncClientTickRateMessage::read);

    public static void write(SyncClientTickRateMessage message, FriendlyByteBuf buf) {
        PacketBufferUtils.writeTag(buf, message.compound);
    }

    public static SyncClientTickRateMessage read(FriendlyByteBuf buf) {
        return new SyncClientTickRateMessage(PacketBufferUtils.readTag(buf));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncClientTickRateMessage message, IPayloadContext context) {
    }
}
