package com.github.alexthe666.citadel.server.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AnimationMessage(int entityID, int index) implements CustomPacketPayload {
    public static final Type<AnimationMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("citadel", "animation"));
    public static final StreamCodec<FriendlyByteBuf, AnimationMessage> CODEC = StreamCodec.ofMember(AnimationMessage::write, AnimationMessage::read);

    public static AnimationMessage read(FriendlyByteBuf buf) {
        return new AnimationMessage(buf.readInt(), buf.readInt());
    }

    public static void write(AnimationMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityID);
        buf.writeInt(message.index);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AnimationMessage message, IPayloadContext context) {
    }
}
