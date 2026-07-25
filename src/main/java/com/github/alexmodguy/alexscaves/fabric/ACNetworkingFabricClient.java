package com.github.alexmodguy.alexscaves.fabric;

import com.github.alexmodguy.alexscaves.server.message.BeholderSyncMessage;
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
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class ACNetworkingFabricClient {
    private static boolean initialized;

    private ACNetworkingFabricClient() {
    }

    public static void registerClient() {
        if (initialized) {
            return;
        }
        initialized = true;

        registerReceiver(BeholderSyncMessage.TYPE, BeholderSyncMessage::handle);
        registerReceiver(SpelunkeryTableCompleteTutorialMessage.TYPE, SpelunkeryTableCompleteTutorialMessage::handle);
        registerReceiver(SundropRainbowMessage.TYPE, SundropRainbowMessage::handle);
        registerReceiver(UpdateBossBarMessage.TYPE, UpdateBossBarMessage::handle);
        registerReceiver(UpdateBossEruptionStatus.TYPE, UpdateBossEruptionStatus::handle);
        registerReceiver(UpdateCaveBiomeMapTagMessage.TYPE, UpdateCaveBiomeMapTagMessage::handle);
        registerReceiver(UpdateEffectVisualityEntityMessage.TYPE, UpdateEffectVisualityEntityMessage::handle);
        registerReceiver(UpdateItemTagMessage.TYPE, UpdateItemTagMessage::handle);
        registerReceiver(UpdateMagneticDataMessage.TYPE, UpdateMagneticDataMessage::handle);
        registerReceiver(PropertiesMessage.TYPE, PropertiesMessage::handle);
        registerReceiver(WorldEventMessage.TYPE, WorldEventMessage::handle);
    }

    private static <T extends CustomPacketPayload> void registerReceiver(CustomPacketPayload.Type<T> type, ACNetworkingFabric.PayloadHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
            ACNetworkingFabric.handleClientbound(payload, context.player(), handler)
        );
    }
}
