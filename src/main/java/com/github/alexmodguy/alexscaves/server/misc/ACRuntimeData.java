package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Stand-in for NeoForge's Entity#getPersistentData: a compound saved with the entity and kept across death and respawn.
 */
public final class ACRuntimeData {
    private static final AttachmentType<CompoundTag> ENTITY_DATA = AttachmentRegistry.<CompoundTag>builder()
            .persistent(CompoundTag.CODEC)
            .copyOnDeath()
            .initializer(CompoundTag::new)
            .buildAndRegister(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "persistent_data"));

    private ACRuntimeData() {
    }

    /** Forces the attachment type to be registered before any entity is loaded. */
    public static void init() {
    }

    /** Read-only access: never creates (or persists) an attachment on the entity. */
    public static CompoundTag read(Entity entity) {
        CompoundTag tag = entity.getAttached(ENTITY_DATA);
        return tag == null ? new CompoundTag() : tag;
    }

    /** Write access: creates the attachment on first use. */
    public static CompoundTag getOrCreate(Entity entity) {
        return entity.getAttachedOrCreate(ENTITY_DATA);
    }
}
