package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.IrradiatedEffect;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Message to sync effect visuals from server to client.
 * For BUBBLED effect, uses a client-side tracking system instead of MobEffect
 * to properly handle effect expiration timing.
 */
public class UpdateEffectVisualityEntityMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateEffectVisualityEntityMessage> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "update_effect_visuality_entity"));

    public static final StreamCodec<FriendlyByteBuf, UpdateEffectVisualityEntityMessage> CODEC =
        StreamCodec.ofMember(UpdateEffectVisualityEntityMessage::write, UpdateEffectVisualityEntityMessage::read);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    private int entityID;
    private int fromEntityID;
    private int potionType;
    private int duration;

    private boolean remove;

    public UpdateEffectVisualityEntityMessage(int entityID, int fromEntityID, int potionType, int duration) {
        this(entityID, fromEntityID, potionType, duration, false);
    }

    public UpdateEffectVisualityEntityMessage(int entityID, int fromEntityID, int potionType, int duration, boolean remove) {
        this.entityID = entityID;
        this.fromEntityID = fromEntityID;
        this.potionType = potionType;
        this.duration = duration;
        this.remove = remove;
    }


    public static UpdateEffectVisualityEntityMessage read(FriendlyByteBuf buf) {
        return new UpdateEffectVisualityEntityMessage(buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean());
    }

    public static void write(UpdateEffectVisualityEntityMessage message, FriendlyByteBuf buf) {
        buf.writeInt(message.entityID);
        buf.writeInt(message.fromEntityID);
        buf.writeInt(message.potionType);
        buf.writeInt(message.duration);
        buf.writeBoolean(message.remove);
    }

    public static void handle(UpdateEffectVisualityEntityMessage message, IPayloadContext context) {
        // This packet is sent from server to client
        if (!context.flow().isClientbound()) {
            return;
        }
        Player playerSided = AlexsCaves.PROXY.getClientSidePlayer();
        if (playerSided != null) {
            Entity entity = playerSided.level().getEntity(message.entityID);
            Entity senderEntity = playerSided.level().getEntity(message.fromEntityID);
            if (entity instanceof LivingEntity living && senderEntity != null && senderEntity.distanceTo(living) < 32) {
                Holder<MobEffect> mobEffect = null;
                int level = 0;
                switch (message.potionType) {
                    case 0:
                        mobEffect = ACEffectRegistry.IRRADIATED;
                        break;
                    case 1:
                        // For BUBBLED effect, use the client-side visual tracking system
                        // This avoids the issue where client-side MobEffect doesn't sync with server expiration
                        boolean isNewEffect = !AlexsCaves.PROXY.hasBubbledEffectVisual(message.entityID);
                        if (message.remove) {
                            AlexsCaves.PROXY.setBubbledEffectTicks(message.entityID, 0);
                        } else {
                            AlexsCaves.PROXY.setBubbledEffectTicks(message.entityID, message.duration);
                            if (isNewEffect) {
                                entity.playSound(ACSoundRegistry.SEA_STAFF_BUBBLE.get());
                            }
                        }
                        return; // Don't use the normal MobEffect system for BUBBLED
                    case 2:
                        mobEffect = ACEffectRegistry.MAGNETIZING;
                        break;
                    case 3:
                        mobEffect = ACEffectRegistry.STUNNED;
                        break;
                    case 4:
                        mobEffect = ACEffectRegistry.IRRADIATED;
                        level = IrradiatedEffect.BLUE_LEVEL;
                        break;
                }
                if (mobEffect != null && living != playerSided) {
                    if (message.remove) {
                        living.removeEffectNoUpdate(mobEffect);
                    } else {
                        living.addEffect(new MobEffectInstance(mobEffect, message.duration, level));
                    }
                }
            }
        }
    }

}
