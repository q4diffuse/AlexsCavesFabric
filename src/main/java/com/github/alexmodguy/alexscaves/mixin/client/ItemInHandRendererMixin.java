package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.event.ClientEvents;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fabric replacement for NeoForge's RenderHandEvent
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"), cancellable = true)
    private void ac_renderHandsWithItems(float partialTicks, PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LocalPlayer player, int packedLight, CallbackInfo ci) {
        if (ClientEvents.isCameraPossessed()) {
            ci.cancel();
        }
    }
}
