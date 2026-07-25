package com.github.alexthe666.citadel.server.message;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DanceJukeboxMessage(int entityId, boolean dancing, BlockPos pos) implements CustomPacketPayload {
    public static final Type<DanceJukeboxMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("citadel", "dance_jukebox"));
    public static final StreamCodec<FriendlyByteBuf, DanceJukeboxMessage> CODEC = StreamCodec.ofMember(DanceJukeboxMessage::write, DanceJukeboxMessage::read);

    public static DanceJukeboxMessage read(FriendlyByteBuf buf) {
        return new DanceJukeboxMessage(buf.readInt(), buf.readBoolean(), buf.readBlockPos());
    }

    public static void write(DanceJukeboxMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityId);
        buf.writeBoolean(message.dancing);
        buf.writeBlockPos(message.pos == null ? BlockPos.ZERO : message.pos);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DanceJukeboxMessage message, IPayloadContext context) {
    }
}
