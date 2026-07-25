package com.github.alexmodguy.alexscaves.citadel.server.message;

import com.github.alexmodguy.alexscaves.citadel.server.entity.CitadelEntityData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PropertiesMessage(String identifier, CompoundTag tag, int entityId) implements CustomPacketPayload {
    public static final Type<PropertiesMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("alexscaves", "citadel_properties"));
    public static final StreamCodec<FriendlyByteBuf, PropertiesMessage> CODEC = StreamCodec.ofMember(PropertiesMessage::write, PropertiesMessage::read);

    public static PropertiesMessage read(FriendlyByteBuf buf) {
        return new PropertiesMessage(buf.readUtf(), PacketBufferUtils.readTag(buf), buf.readInt());
    }

    public static void write(PropertiesMessage message, FriendlyByteBuf buf) {
        buf.writeUtf(message.identifier);
        PacketBufferUtils.writeTag(buf, message.tag == null ? new CompoundTag() : message.tag);
        buf.writeInt(message.entityId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PropertiesMessage message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player() == null ? null : context.player().level().getEntity(message.entityId);
            if (!(entity instanceof LivingEntity livingEntity)) {
                return;
            }
            if ("CitadelPatreonConfig".equals(message.identifier) || "CitadelTagUpdate".equals(message.identifier)) {
                CitadelEntityData.setCitadelTag(livingEntity, message.tag);
            }
        });
    }
}
