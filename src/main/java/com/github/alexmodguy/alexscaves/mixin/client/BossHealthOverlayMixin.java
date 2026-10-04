package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.event.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

// Fabric replacement for NeoForge's CustomizeGuiOverlayEvent.BossEventProgress
@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    Map<UUID, LerpingBossEvent> events;

    @Shadow
    private void drawBar(GuiGraphics guiGraphics, int x, int y, BossEvent bossEvent) {
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void ac_render(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (events.isEmpty() || events.keySet().stream().noneMatch(ClientProxy.bossBarRenderTypes::containsKey)) {
            return;
        }
        // same loop as vanilla, letting AC draw its own bars
        ci.cancel();
        minecraft.getProfiler().push("bossHealth");
        int i = guiGraphics.guiWidth();
        int j = 12;
        for (LerpingBossEvent bossEvent : events.values()) {
            int k = i / 2 - 91;
            int increment = ClientEvents.renderBossBar(guiGraphics, k, j, bossEvent);
            if (increment < 0) {
                this.drawBar(guiGraphics, k, j, bossEvent);
                Component component = bossEvent.getName();
                int l = minecraft.font.width(component);
                guiGraphics.drawString(minecraft.font, component, i / 2 - l / 2, j - 9, 16777215);
                increment = 10 + minecraft.font.lineHeight;
            }
            j += increment;
            if (j >= guiGraphics.guiHeight() / 3) {
                break;
            }
        }
        minecraft.getProfiler().pop();
    }
}
