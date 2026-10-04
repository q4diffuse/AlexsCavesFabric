package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.event.ClientEvents;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Fabric replacement for NeoForge's ComputeFovModifierEvent
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin {

    @ModifyArg(method = "getFieldOfViewModifier", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F"), index = 2)
    private float ac_getFieldOfViewModifier(float fovModifier) {
        return ClientEvents.computeFovModifier((Player) (Object) this, fovModifier);
    }
}
