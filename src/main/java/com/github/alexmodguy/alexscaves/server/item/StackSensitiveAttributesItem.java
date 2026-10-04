package com.github.alexmodguy.alexscaves.server.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Stand-in for NeoForge's IItemExtension#getDefaultAttributeModifiers(ItemStack), applied by ItemStackMixin.
 */
public interface StackSensitiveAttributesItem {
    ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack);
}
