package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.StackSensitiveAttributesItem;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.UnaryOperator;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    // NeoForge reads stack-sensitive default attributes (Primitive Club Swiftwood, Gingerbread armor durability)
    @WrapOperation(method = {"forEachModifier(Lnet/minecraft/world/entity/EquipmentSlotGroup;Ljava/util/function/BiConsumer;)V", "forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getDefaultAttributeModifiers()Lnet/minecraft/world/item/component/ItemAttributeModifiers;"))
    private ItemAttributeModifiers ac_getStackSensitiveAttributes(Item item, Operation<ItemAttributeModifiers> original) {
        if (item instanceof StackSensitiveAttributesItem stackSensitive) {
            return stackSensitive.getDefaultAttributeModifiers((ItemStack) (Object) this);
        }
        return original.call(item);
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void ac_applyCustomRarityStyle(CallbackInfoReturnable<Component> cir) {
        ItemStack stack = (ItemStack) (Object) this;
        UnaryOperator<Style> styleModifier = ACItemRegistry.getCustomRarityStyle(stack);
        if (styleModifier != null) {
            cir.setReturnValue(cir.getReturnValue().copy().withStyle(styleModifier));
        }
    }
}
