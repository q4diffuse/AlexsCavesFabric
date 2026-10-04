package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.event.ClientEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.PlayerRideableJumping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fabric replacement for the NeoForge RenderGuiLayerEvent handlers of the original ClientEvents
@Mixin(Gui.class)
public abstract class GuiMixin {

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    private void ac_renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (ClientEvents.isCameraPossessed()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void ac_renderExperienceBar(GuiGraphics guiGraphics, int x, CallbackInfo ci) {
        if (ClientEvents.shouldHideExperienceBar()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderJumpMeter", at = @At("HEAD"), cancellable = true)
    private void ac_renderJumpMeter(PlayerRideableJumping rideable, GuiGraphics guiGraphics, int x, CallbackInfo ci) {
        if (ClientEvents.isCameraPossessed()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    private void ac_renderSelectedItemName(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (ClientEvents.isCameraPossessed()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderItemHotbar", at = @At("TAIL"))
    private void ac_renderItemHotbar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        ClientEvents.renderHotbarOverlays(guiGraphics);
    }

    @Inject(method = "renderPlayerHealth", at = @At("TAIL"))
    private void ac_renderPlayerHealth(GuiGraphics guiGraphics, CallbackInfo ci) {
        ClientEvents.renderIrradiatedHearts(guiGraphics);
    }
}
