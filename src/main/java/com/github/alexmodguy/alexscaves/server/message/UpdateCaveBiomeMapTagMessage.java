package com.github.alexmodguy.alexscaves.server.message;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.citadel.server.message.PacketBufferUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class UpdateCaveBiomeMapTagMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<UpdateCaveBiomeMapTagMessage> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "update_cave_biome_map_tag"));

    public static final StreamCodec<FriendlyByteBuf, UpdateCaveBiomeMapTagMessage> CODEC =
        StreamCodec.ofMember(UpdateCaveBiomeMapTagMessage::write, UpdateCaveBiomeMapTagMessage::read);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }

    private UUID userUUID;
    private UUID caveBiomeMapUUID;
    private CompoundTag tag;

    public UpdateCaveBiomeMapTagMessage(UUID userUUID, UUID caveBiomeMapUUID, CompoundTag tag) {
        this.userUUID = userUUID;
        this.caveBiomeMapUUID = caveBiomeMapUUID;
        this.tag = tag;
    }


    public static UpdateCaveBiomeMapTagMessage read(FriendlyByteBuf buf) {
        return new UpdateCaveBiomeMapTagMessage(buf.readUUID(), buf.readUUID(), PacketBufferUtils.readTag(buf));
    }

    public static void write(UpdateCaveBiomeMapTagMessage message, FriendlyByteBuf buf) {
        buf.writeUUID(message.userUUID);
        buf.writeUUID(message.caveBiomeMapUUID);
        PacketBufferUtils.writeTag(buf, message.tag);
    }

    public static void handle(UpdateCaveBiomeMapTagMessage message, IPayloadContext context) {
        // This packet is sent from server to client
        if (!context.flow().isClientbound()) {
            return;
        }
        Player playerSided = AlexsCaves.PROXY.getClientSidePlayer();
        if (playerSided != null) {
            Player player = playerSided.level().getPlayerByUUID(message.userUUID);
            if (player != null) {
                ItemStack set = null;
                for(int i = 0; i < player.getInventory().items.size(); i++){
                    ItemStack itemStack = player.getInventory().items.get(i);
                    if(itemStack.is(ACItemRegistry.CAVE_MAP.get()) && itemStack.has(DataComponents.CUSTOM_DATA)){
                        CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                        if(tag.contains("MapUUID") && message.caveBiomeMapUUID.equals(tag.getUUID("MapUUID"))){
                            set = itemStack;
                            break;
                        }
                    }
                }
                if(set != null){
                    set.set(DataComponents.CUSTOM_DATA, CustomData.of(message.tag));
                }
            }
        }
    }

}
