package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.client.event.ClientEvents;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Fabric replacement for Citadel's EventPosePlayerHand
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {

    @Inject(method = "poseRightArm", at = @At("HEAD"), cancellable = true)
    private void ac_poseRightArm(LivingEntity entity, CallbackInfo ci) {
        if (ClientEvents.poseHumanoidArms(entity, (HumanoidModel<?>) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "poseLeftArm", at = @At("HEAD"), cancellable = true)
    private void ac_poseLeftArm(LivingEntity entity, CallbackInfo ci) {
        if (ClientEvents.poseHumanoidArms(entity, (HumanoidModel<?>) (Object) this)) {
            ci.cancel();
        }
    }
}
