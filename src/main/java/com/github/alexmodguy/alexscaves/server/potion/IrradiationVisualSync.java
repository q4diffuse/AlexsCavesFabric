package com.github.alexmodguy.alexscaves.server.potion;

import com.github.alexmodguy.alexscaves.server.message.UpdateEffectVisualityEntityMessage;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

/**
 * Vanilla only sends mob effects to the affected player, so other clients never learn that a mob is irradiated
 * and cannot draw its glow. This mirrors the glow state (none / green / blue) to the players tracking the entity,
 * sending only when that state changes.
 */
public final class IrradiationVisualSync {

    public static final int NONE = 0;
    public static final int GREEN = 1;
    public static final int BLUE = 2;

    private IrradiationVisualSync() {
    }

    public static int visualLevel(@Nullable MobEffectInstance instance) {
        if (instance == null) {
            return NONE;
        }
        return instance.getAmplifier() + 1 >= IrradiatedEffect.BLUE_LEVEL ? BLUE : GREEN;
    }

    public static int visualLevel(LivingEntity entity) {
        return visualLevel(entity.getEffect(ACEffectRegistry.IRRADIATED));
    }

    public static void onVisualChanged(LivingEntity entity, int previous, int current) {
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            // the client only ever raises an effect's amplifier, so going from blue to green needs a reset first
            if (current != NONE && current < previous) {
                PacketDistributor.sendToPlayer(player, removeMessage(entity));
            }
            PacketDistributor.sendToPlayer(player, current == NONE ? removeMessage(entity) : addMessage(entity, current));
        }
    }

    public static void onStartTracking(Entity entity, ServerPlayer player) {
        if (entity instanceof LivingEntity living) {
            int current = visualLevel(living);
            if (current != NONE) {
                PacketDistributor.sendToPlayer(player, addMessage(living, current));
            }
        }
    }

    private static UpdateEffectVisualityEntityMessage addMessage(LivingEntity entity, int visual) {
        MobEffectInstance instance = entity.getEffect(ACEffectRegistry.IRRADIATED);
        int duration = instance == null ? 0 : instance.getDuration();
        return new UpdateEffectVisualityEntityMessage(entity.getId(), entity.getId(), visual == BLUE ? 4 : 0, duration);
    }

    private static UpdateEffectVisualityEntityMessage removeMessage(LivingEntity entity) {
        return new UpdateEffectVisualityEntityMessage(entity.getId(), entity.getId(), 0, 0, true);
    }
}
