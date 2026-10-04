package com.github.alexmodguy.alexscaves.server.item;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GingerbreadArmorItem extends ArmorItem implements StackSensitiveAttributesItem {

    private static final double MIN_SPEED_BOOST = 0.1D;
    private static final double MAX_SPEED_BOOST = 1.0D;
    private final Map<Integer, ItemAttributeModifiers> gingerbreadDurabilityDependentAttributes = new HashMap<>();
    private final ItemAttributeModifiers defaultItemAttributes;
    private final ACArmorMaterial acMaterial;

    public GingerbreadArmorItem(ACArmorMaterial armorMaterial, Type slot) {
        super(armorMaterial.getHolder(), slot, new Properties().durability(armorMaterial.getDurabilityForType(slot)));
        this.acMaterial = armorMaterial;
        this.defaultItemAttributes = createGingerbreadAttributes(armorMaterial, slot, MIN_SPEED_BOOST);
    }

    private static ItemAttributeModifiers createGingerbreadAttributes(ACArmorMaterial armorMaterial, Type slot, double speedBoost) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "armor_gingerbread_" + slot.getName());
        EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(slot.getSlot());
        return ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR, new AttributeModifier(id, armorMaterial.getDefenseForType(slot), AttributeModifier.Operation.ADD_VALUE), slotGroup)
                .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(id, speedBoost, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), slotGroup)
                .build();
    }

    private ItemAttributeModifiers getOrCreateDurabilityAttributes(int durabilityIn, int maxDurability) {
        if (gingerbreadDurabilityDependentAttributes.containsKey(durabilityIn)) {
            return gingerbreadDurabilityDependentAttributes.get(durabilityIn);
        } else {
            float scaledDurability = durabilityIn / (float) maxDurability;
            double speed = MIN_SPEED_BOOST + (MAX_SPEED_BOOST - MIN_SPEED_BOOST) * scaledDurability;
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "armor_gingerbread_" + type.getName() + "_durability_" + durabilityIn);
            EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(type.getSlot());
            ItemAttributeModifiers attributes = ItemAttributeModifiers.builder()
                    .add(Attributes.ARMOR, new AttributeModifier(id, this.getDefense(), AttributeModifier.Operation.ADD_VALUE), slotGroup)
                    .add(Attributes.MOVEMENT_SPEED, new AttributeModifier(id, speed, AttributeModifier.Operation.ADD_MULTIPLIED_BASE), slotGroup)
                    .build();
            gingerbreadDurabilityDependentAttributes.put(durabilityIn, attributes);
            return attributes;
        }
    }

    public void initializeClient(java.util.function.Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions) AlexsCaves.PROXY.getArmorProperties());
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        if (stack.getDamageValue() > 0) {
            return getOrCreateDurabilityAttributes(stack.getDamageValue(), stack.getMaxDamage());
        }
        return defaultItemAttributes;
    }

    @Nullable
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        if (slot == EquipmentSlot.LEGS) {
            return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/gingerbread_armor_1.png");
        } else {
            return ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/armor/gingerbread_armor_0.png");
        }
    }
}
