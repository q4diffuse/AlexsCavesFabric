package com.github.alexmodguy.alexscaves.mixin.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.event.ClientEvents;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessesCamera;
import com.github.alexmodguy.alexscaves.server.entity.util.ShakesScreen;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {

    // In 1.21, move() parameters changed from double to float
    @Shadow
    protected abstract void move(float zoom, float dy, float dx);

    @Shadow
    protected abstract void setPosition(Vec3 vec3);

    // In 1.21, getMaxZoom() parameter changed from double to float
    @Shadow
    private float getMaxZoom(float maxZoom) {
        return 0;
    }

    @Shadow
    protected abstract void setRotation(float p_90573_, float p_90574_);

    @Shadow
    private float yRot;

    @Shadow
    private float xRot;

    @Shadow
    private boolean initialized;

    @Inject(
            method = {"Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void ac_onSyncedDataUpdated(BlockGetter level, Entity entity, boolean detatched, boolean mirrored, float partialTicks, CallbackInfo ci) {
        // Handle magnetic attachment
        Direction dir = MagnetUtil.getEntityMagneticDirection(entity);
        if (dir != Direction.DOWN && dir != Direction.UP) {
            this.setPosition(MagnetUtil.getEyePositionForAttachment(entity, dir, partialTicks));
            if (detatched) {
                if (mirrored) {
                    this.setRotation(this.yRot + 180.0F, -this.xRot);
                }
                // In 1.21, move() and getMaxZoom() use float instead of double
                this.move(-this.getMaxZoom(4.0F), 0.0F, 0.0F);
            }
        }

        // Handle screen shake - must be done at TAIL after setPosition() is called
        // In ComputeCameraAngles event, move() effects are overwritten by subsequent setPosition()
        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player != null && AlexsCaves.CLIENT_CONFIG.screenShaking.get()) {
            float tremorAmount = ClientProxy.renderNukeShakeFor > 0 ? 1.5F : 0F;
            if (player instanceof PossessesCamera watcherEntity) {
                tremorAmount = watcherEntity.isPossessionBreakable()
                        ? AlexsCaves.PROXY.getPossessionStrengthAmount(partialTicks)
                        : 0F;
            }
            if (tremorAmount == 0) {
                double shakeDistanceScale = 64;
                double distance = Double.MAX_VALUE;
                AABB aabb = player.getBoundingBox().inflate(shakeDistanceScale);
                for (Mob screenShaker : Minecraft.getInstance().level.getEntitiesOfClass(Mob.class, aabb,
                        (mob -> mob instanceof ShakesScreen))) {
                    ShakesScreen shakesScreen = (ShakesScreen) screenShaker;
                    if (shakesScreen.canFeelShake(player) && screenShaker.distanceTo(player) < distance) {
                        distance = screenShaker.distanceTo(player);
                        tremorAmount = Math.min((1F - (float) Math.min(1, distance / shakesScreen.getShakeDistance()))
                                * Math.max(shakesScreen.getScreenShakeAmount(partialTicks), 0F), 2.0F);
                    }
                }
            }
            if (tremorAmount > 0) {
                if (ClientProxy.lastTremorTick != player.tickCount) {
                    RandomSource rng = player.level().random;
                    ClientProxy.randomTremorOffsets[0] = rng.nextFloat();
                    ClientProxy.randomTremorOffsets[1] = rng.nextFloat();
                    ClientProxy.randomTremorOffsets[2] = rng.nextFloat();
                    ClientProxy.lastTremorTick = player.tickCount;
                }
                float intensity = (float) (tremorAmount * Minecraft.getInstance().options.screenEffectScale().get());
                this.move(
                        ClientProxy.randomTremorOffsets[0] * 0.2F * intensity,
                        ClientProxy.randomTremorOffsets[1] * 0.2F * intensity,
                        ClientProxy.randomTremorOffsets[2] * 0.5F * intensity);
            }
        }
        ClientEvents.computeCameraAngles((Camera) (Object) this, player, partialTicks);
    }

    @Inject(
            method = {"Lnet/minecraft/client/Camera;getFluidInCamera()Lnet/minecraft/world/level/material/FogType;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_getFluidInCamera(CallbackInfoReturnable<FogType> cir) {
        if (initialized && Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(ACEffectRegistry.BUBBLED) && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            cir.setReturnValue(FogType.WATER);
        }
    }
}
