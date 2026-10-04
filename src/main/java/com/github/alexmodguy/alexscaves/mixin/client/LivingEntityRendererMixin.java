package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.event.ClientEvents;
import com.github.alexmodguy.alexscaves.client.render.item.RaygunRenderHelper;
import com.github.alexmodguy.alexscaves.client.render.entity.LivingEntityRendererAccessor;
import com.github.alexmodguy.alexscaves.server.entity.util.FlyingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.HeadRotationEntityAccessor;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityAccessor;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin extends EntityRenderer implements LivingEntityRendererAccessor {

    @Shadow protected abstract void scale(LivingEntity living, PoseStack poseStack, float f);
    @Shadow protected abstract boolean addLayer(RenderLayer<?, ?> renderLayer);

    protected LivingEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    public void scaleForHologram(LivingEntity entity, PoseStack poseStack, float partialTicks) {
        this.scale(entity, poseStack, partialTicks);
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void ac_preRender(LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        // entities drawn by another mob's render layer (held mobs, riders) must not also render on their own
        if (ClientProxy.blockedEntityRenders.contains(entity.getUUID())) {
            ClientProxy.blockedEntityRenders.remove(entity.getUUID());
            if (!AlexsCaves.PROXY.isFirstPersonPlayer(entity)) {
                ac_renderPostEffects(entity, partialTicks, poseStack, bufferSource, packedLight);
                ci.cancel();
                return;
            }
        }
        if (entity instanceof HeadRotationEntityAccessor magnetic) {
            magnetic.setMagnetHeadRotation();
        }
    }

    @Inject(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN")
    )
    private void ac_postRender(LivingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (entity instanceof HeadRotationEntityAccessor magnetic) {
            magnetic.resetMagnetHeadRotation();
        }
        ac_renderPostEffects(entity, partialTicks, poseStack, bufferSource, packedLight);
    }

    // what the original drew in RenderLivingEvent.Post, in the untransformed entity space
    private static void ac_renderPostEffects(LivingEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            RaygunRenderHelper.renderRaysFor(entity, entity.getPosition(partialTicks), poseStack, bufferSource, partialTicks, false, 0);
        }
        ClientEvents.renderDarknessTrail(entity, partialTicks, poseStack, bufferSource, packedLight);
    }

    // NeoForge replaces every isPassenger() check in render with "is passenger of a vehicle that lets riders sit"
    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isPassenger()Z")
    )
    private boolean ac_shouldRiderSit(boolean isPassenger, @Local(argsOnly = true) LivingEntity entity) {
        return isPassenger && !(entity.getVehicle() instanceof FlyingMount mount && !mount.shouldRiderSit());
    }

    @Inject(method = "setupRotations", at = @At("RETURN"))
    private void ac_setupMagneticRotations(LivingEntity entity, PoseStack poseStack, float bob, float yBodyRot,
            float partialTick, float scale, CallbackInfo ci) {
        if (entity instanceof MagneticEntityAccessor magnetic) {
            float width = entity.getBbWidth();
            float height = entity.getBbHeight();
            float progress = magnetic.getAttachmentProgress(partialTick);
            float prevProg = 1F - progress;
            float bodyRot = 180.0F - yBodyRot;
            if (magnetic.getMagneticAttachmentFace().getAxis() != Direction.Axis.Y) {
                poseStack.mulPose(Axis.YN.rotationDegrees(bodyRot));
            }
            ac_rotateForAngle(entity, poseStack, magnetic.getPrevMagneticAttachmentFace(), prevProg, height);
            ac_rotateForAngle(entity, poseStack, magnetic.getMagneticAttachmentFace(), progress, height);
        }
    }

    private static void ac_rotateForAngle(LivingEntity entity, PoseStack poseStack, Direction rotate, float f, float height) {
        boolean down = entity.zza < 0.0F;
        switch (rotate) {
            case DOWN:
                break;
            case UP:
                poseStack.translate(0.0D, height * f, 0.0D);
                poseStack.mulPose(Axis.XP.rotationDegrees(-180.0F * f));
                poseStack.mulPose(Axis.YP.rotationDegrees(-180.0F * f));
                break;
            case NORTH:
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F * f));
                poseStack.translate(0.0D, -0.25f * f, 0.0D);
                if (down) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F * f));
                }
                break;
            case SOUTH:
                poseStack.mulPose(Axis.YP.rotationDegrees(180 * f));
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F * f));
                poseStack.translate(0.0D, -0.25f * f, 0.0D);
                if (down) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F * f));
                }
                break;
            case WEST:
                poseStack.mulPose(Axis.YP.rotationDegrees(90 * f));
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F * f));
                poseStack.translate(0.0D, -0.25f * f, 0.0D);
                if (down) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F * f));
                }
                break;
            case EAST:
                poseStack.mulPose(Axis.YP.rotationDegrees(-90 * f));
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F * f));
                poseStack.translate(0.0D, -0.25f * f, 0.0D);
                if (down) {
                    poseStack.mulPose(Axis.YP.rotationDegrees(180.0F * f));
                }
                break;
        }
    }

    @Override
    public void addACLayer(RenderLayer<?, ?> layer) {
        this.addLayer(layer);
    }
}
