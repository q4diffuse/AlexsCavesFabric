package com.github.alexmodguy.alexscaves.fabric;

import com.github.alexmodguy.alexscaves.server.message.ArmorKeyMessage;
import com.github.alexmodguy.alexscaves.server.message.BeholderRotateMessage;
import com.github.alexmodguy.alexscaves.server.message.BeholderSyncMessage;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.message.MultipartEntityMessage;
import com.github.alexmodguy.alexscaves.server.message.PlayerJumpFromMagnetMessage;
import com.github.alexmodguy.alexscaves.server.message.PossessionKeyMessage;
import com.github.alexmodguy.alexscaves.server.message.SpelunkeryTableChangeMessage;
import com.github.alexmodguy.alexscaves.server.message.SpelunkeryTableCompleteTutorialMessage;
import com.github.alexmodguy.alexscaves.server.message.SundropRainbowMessage;
import com.github.alexmodguy.alexscaves.server.message.UpdateBossBarMessage;
import com.github.alexmodguy.alexscaves.server.message.UpdateBossEruptionStatus;
import com.github.alexmodguy.alexscaves.server.message.UpdateCaveBiomeMapTagMessage;
import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import com.github.alexmodguy.alexscaves.server.message.UpdateItemTagMessage;
import com.github.alexmodguy.alexscaves.server.message.UpdateMagneticDataMessage;
import com.github.alexmodguy.alexscaves.server.message.WorldEventMessage;
import com.github.alexmodguy.alexscaves.citadel.server.message.PropertiesMessage;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ACNetworkingFabric {
    private static final IPayloadContext.PayloadFlow CLIENTBOUND_FLOW = () -> true;
    private static final IPayloadContext.PayloadFlow SERVERBOUND_FLOW = () -> false;
    private static boolean initialized;

    private ACNetworkingFabric() {
    }

    public static void registerCommon() {
        if (initialized) {
            return;
        }
        initialized = true;

        registerC2S(ArmorKeyMessage.TYPE, ArmorKeyMessage.CODEC, ArmorKeyMessage::handle);
        registerC2S(BeholderRotateMessage.TYPE, BeholderRotateMessage.CODEC, BeholderRotateMessage::handle);
        registerC2S(MountedEntityKeyMessage.TYPE, MountedEntityKeyMessage.CODEC, MountedEntityKeyMessage::handle);
        registerC2S(MultipartEntityMessage.TYPE, MultipartEntityMessage.CODEC, MultipartEntityMessage::handle);
        registerC2S(PlayerJumpFromMagnetMessage.TYPE, PlayerJumpFromMagnetMessage.CODEC, PlayerJumpFromMagnetMessage::handle);
        registerC2S(PossessionKeyMessage.TYPE, PossessionKeyMessage.CODEC, PossessionKeyMessage::handle);
        registerC2S(SpelunkeryTableChangeMessage.TYPE, SpelunkeryTableChangeMessage.CODEC, SpelunkeryTableChangeMessage::handle);

        registerS2C(BeholderSyncMessage.TYPE, BeholderSyncMessage.CODEC);
        registerS2C(SpelunkeryTableCompleteTutorialMessage.TYPE, SpelunkeryTableCompleteTutorialMessage.CODEC);
        registerS2C(SundropRainbowMessage.TYPE, SundropRainbowMessage.CODEC);
        registerS2C(UpdateBossBarMessage.TYPE, UpdateBossBarMessage.CODEC);
        registerS2C(UpdateBossEruptionStatus.TYPE, UpdateBossEruptionStatus.CODEC);
        registerS2C(UpdateCaveBiomeMapTagMessage.TYPE, UpdateCaveBiomeMapTagMessage.CODEC);
        registerS2C(UpdateEffectVisualityEntityMessage.TYPE, UpdateEffectVisualityEntityMessage.CODEC);
        registerS2C(UpdateMagneticDataMessage.TYPE, UpdateMagneticDataMessage.CODEC);
        registerS2C(WorldEventMessage.TYPE, WorldEventMessage.CODEC);

        registerBidirectional(PropertiesMessage.TYPE, PropertiesMessage.CODEC, PropertiesMessage::handle);
        registerBidirectional(UpdateItemTagMessage.TYPE, UpdateItemTagMessage.CODEC, UpdateItemTagMessage::handle);
    }

    static <T extends CustomPacketPayload> void handleClientbound(T payload, Player player, PayloadHandler<T> handler) {
        handler.handle(payload, new FabricPayloadContext(player, CLIENTBOUND_FLOW));
    }

    private static <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<T> handler) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
            handler.handle(payload, new FabricPayloadContext(context.player(), SERVERBOUND_FLOW))
        );
    }

    private static <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(type, codec);
    }

    private static <T extends CustomPacketPayload> void registerBidirectional(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, PayloadHandler<T> handler) {
        registerC2S(type, codec, handler);
        registerS2C(type, codec);
    }

    @FunctionalInterface
    interface PayloadHandler<T extends CustomPacketPayload> {
        void handle(T payload, IPayloadContext context);
    }

    private record FabricPayloadContext(Player player, PayloadFlow flow) implements IPayloadContext {
    }
}
