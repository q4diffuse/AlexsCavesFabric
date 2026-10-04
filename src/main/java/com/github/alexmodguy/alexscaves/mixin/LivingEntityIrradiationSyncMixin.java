package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.IrradiationVisualSync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityIrradiationSyncMixin {

    // glow state the tracking clients were last told about
    @Unique
    private int ac$irradiationVisual = IrradiationVisualSync.NONE;

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void ac$onEffectAdded(MobEffectInstance effectInstance, Entity source, CallbackInfo ci) {
        ac$updateIrradiationVisual(IrradiationVisualSync.visualLevel((LivingEntity) (Object) this));
    }

    @Inject(method = "onEffectUpdated", at = @At("TAIL"))
    private void ac$onEffectUpdated(MobEffectInstance effectInstance, boolean forced, Entity source, CallbackInfo ci) {
        ac$updateIrradiationVisual(IrradiationVisualSync.visualLevel((LivingEntity) (Object) this));
    }

    @Inject(method = "onEffectRemoved", at = @At("TAIL"))
    private void ac$onEffectRemoved(MobEffectInstance effectInstance, CallbackInfo ci) {
        // removeAllEffects (milk, /effect clear) calls this before taking the effect out of the map
        if (effectInstance.is(ACEffectRegistry.IRRADIATED)) {
            ac$updateIrradiationVisual(IrradiationVisualSync.NONE);
        }
    }

    // effects loaded from disk skip onEffectAdded; new trackers are synced by the start-tracking hook
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void ac$readIrradiationVisual(CompoundTag tag, CallbackInfo ci) {
        ac$irradiationVisual = IrradiationVisualSync.visualLevel((LivingEntity) (Object) this);
    }

    @Unique
    private void ac$updateIrradiationVisual(int current) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide) {
            return;
        }
        if (current != ac$irradiationVisual) {
            int previous = ac$irradiationVisual;
            ac$irradiationVisual = current;
            IrradiationVisualSync.onVisualChanged(self, previous, current);
        }
    }
}
