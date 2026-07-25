package com.github.alexmodguy.alexscaves.mixin.client;


import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.entity.SubmarineRenderer;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.ACPotionEffectLayer;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Shadow
    private float darkenWorldAmount;

    @Shadow
    @Final
    private RenderBuffers renderBuffers;

    @Shadow
    @Final
    private Camera mainCamera;

    @Inject(
            method = {"Lnet/minecraft/client/renderer/GameRenderer;tick()V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void ac_tick(CallbackInfo ci) {
        if (((ClientProxy) AlexsCaves.PROXY).renderNukeSkyDarkFor > 0 && darkenWorldAmount < 1.0F) {
            darkenWorldAmount = Math.min(darkenWorldAmount + 0.3F, 1.0F);
        }
    }

    // In 1.21, render method signature changed: render(FJZ)V -> render(DeltaTracker, Z)V
    @Inject(
            method = {"Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V"},
            remap = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Lighting;setupFor3DItems()V",
                    shift = At.Shift.AFTER
            )
    )
    public void ac_render(DeltaTracker deltaTracker, boolean renderLevel, CallbackInfo ci) {
        float partialTick = deltaTracker.getGameTimeDeltaPartialTick(true);
        if (renderLevel) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null && AlexsCaves.CLIENT_CONFIG.sugarRushSaturationEffect.get()
                    && minecraft.player.hasEffect(ACEffectRegistry.SUGAR_RUSH)) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.SUGAR_RUSH_SHADER);
            }
            PostEffectRegistry.processEffects(minecraft.getMainRenderTarget());
            PostEffectRegistry.blitEffects();
        }
        ((ClientProxy) AlexsCaves.PROXY).preScreenRender(partialTick);
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/GameRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;)V"},
            remap = true,
            at = @At("HEAD")
    )
    public void ac_beginPostEffects(DeltaTracker deltaTracker, CallbackInfo ci) {
        PostEffectRegistry.beginFrame(Minecraft.getInstance().getMainRenderTarget());
    }


    // In 1.21, renderLevel signature changed: renderLevel(FJPoseStack)V -> renderLevel(DeltaTracker)V
    // Also renderItemInHand changed: renderItemInHand(PoseStack, Camera, F)V -> renderItemInHand(Camera, F, Matrix4f)V
    @Inject(
            method = {"Lnet/minecraft/client/renderer/GameRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;)V"},
            remap = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/Camera;FLorg/joml/Matrix4f;)V",
                    shift = At.Shift.BEFORE
            )
    )
    public void ac_renderLevel(DeltaTracker deltaTracker, CallbackInfo ci) {
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(true);
        Entity player = Minecraft.getInstance().cameraEntity;
        if (player != null && player.isPassenger() && player.getVehicle() instanceof SubmarineEntity submarine && SubmarineRenderer.isFirstPersonFloodlightsMode(submarine)) {
            Vec3 offset = submarine.getPosition(partialTicks).subtract(player.getEyePosition(partialTicks));
            // In 1.21, we need to create our own PoseStack with camera rotation applied
            PoseStack poseStack = new PoseStack();
            // Apply camera rotation (conjugate to get the view matrix rotation)
            Quaternionf cameraRotation = mainCamera.rotation().conjugate(new Quaternionf());
            poseStack.mulPose(cameraRotation);
            poseStack.pushPose();
            poseStack.translate(offset.x, offset.y, offset.z);
            SubmarineRenderer.renderSubFirstPerson(submarine, partialTicks, poseStack, renderBuffers.bufferSource());
            poseStack.popPose();
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/renderer/GameRenderer;renderLevel(Lnet/minecraft/client/DeltaTracker;)V"},
            remap = true,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/Camera;FLorg/joml/Matrix4f;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void ac_renderLevelAfterHand(DeltaTracker deltaTracker, CallbackInfo ci) {
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(true);
        if (Minecraft.getInstance().getCameraEntity() instanceof LivingEntity living && living.hasEffect(ACEffectRegistry.BUBBLED) && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
            // For first-person screen overlay effects, we don't apply camera rotation
            // The effect should stay fixed on screen regardless of where the player looks
            PoseStack poseStack = new PoseStack();
            ACPotionEffectLayer.renderBubbledFirstPerson(poseStack);
            multibuffersource$buffersource.endBatch();
        }
    }
}
