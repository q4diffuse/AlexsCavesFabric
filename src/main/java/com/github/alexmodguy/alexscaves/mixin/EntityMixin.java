package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.entity.util.ACAttachmentRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityData;
import com.github.alexmodguy.alexscaves.server.entity.util.MagnetUtil;
import com.github.alexmodguy.alexscaves.server.entity.util.MagneticEntityAccessor;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.RainbounceBootsItem;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.citadel.server.entity.collision.ICustomCollisions;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements MagneticEntityAccessor {

    @Shadow
    @Final
    protected SynchedEntityData entityData;

    @Shadow
    protected abstract void playStepSound(BlockPos p_20135_, BlockState p_20136_);

    @Shadow
    private Level level;
    @Shadow
    private Vec3 position;

    @Shadow
    private EntityDimensions dimensions;

    @Shadow
    public abstract void tick();

    @Shadow
    public abstract void refreshDimensions();

    @Shadow
    protected boolean wasTouchingWater;

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract boolean onGround();

    @Shadow
    public abstract double getY();

    private float attachChangeProgress = 0F;
    private float prevAttachChangeProgress = 0F;
    private Direction prevAttachDir = Direction.DOWN;
    private int jumpFlipCooldown = 0;

    private BlockPos lastStepPos;
    private Vec3 lastBouncePos;
    
    /**
     * Check if this entity type supports magnetic data (LivingEntity, ItemEntity, or FallingBlockEntity)
     */
    private boolean supportsMagneticData() {
        Entity thisEntity = (Entity) (Object) this;
        return thisEntity instanceof LivingEntity || thisEntity instanceof ItemEntity || thisEntity instanceof FallingBlockEntity;
    }
    
    /**
     * Get the MagneticEntityData attachment for this entity, or null if not supported
     */
    private MagneticEntityData getMagneticData() {
        if (!supportsMagneticData()) {
            return null;
        }
        Entity thisEntity = (Entity) (Object) this;
        return ACAttachmentRegistry.getMagneticData(thisEntity);
    }
    
    /**
     * Sync the magnetic data attachment to clients via network packet.
     * This replaces 1.20's automatic SynchedEntityData sync.
     */
    private void syncMagneticData() {
        if (!supportsMagneticData()) {
            return;
        }
        Entity thisEntity = (Entity) (Object) this;
        // Only sync from server side
        if (!thisEntity.level().isClientSide) {
            MagneticEntityData data = ACAttachmentRegistry.getMagneticData(thisEntity);
            // Send sync packet to all tracking players
            com.github.alexmodguy.alexscaves.server.message.UpdateMagneticDataMessage msg = 
                new com.github.alexmodguy.alexscaves.server.message.UpdateMagneticDataMessage(thisEntity, data);
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingEntityAndSelf(thisEntity, msg);
        }
    }

    @Inject(method = "saveWithoutId", at = @At("RETURN"))
    private void ac_saveWithoutId(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        MagneticEntityData data = getMagneticData();
        if (data != null && !data.isDefault()) {
            tag.putFloat("MagneticDeltaX", data.getDeltaX());
            tag.putFloat("MagneticDeltaY", data.getDeltaY());
            tag.putFloat("MagneticDeltaZ", data.getDeltaZ());
            tag.putInt("MagneticAttachmentDir", data.getAttachmentDirection().ordinal());
        }
    }

    @Inject(method = "load", at = @At("RETURN"))
    private void ac_load(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("MagneticAttachmentDir")) {
            MagneticEntityData data = getMagneticData();
            if (data != null) {
                data.setDeltaX(tag.getFloat("MagneticDeltaX"));
                data.setDeltaY(tag.getFloat("MagneticDeltaY"));
                data.setDeltaZ(tag.getFloat("MagneticDeltaZ"));
                data.setAttachmentDirection(Direction.values()[tag.getInt("MagneticAttachmentDir")]);
            }
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;tick()V"},
            remap = true,
            at = @At(value = "TAIL")
    )
    public void ac_tick(CallbackInfo ci) {
        Entity thisEntity = (Entity) (Object) this;
        prevAttachChangeProgress = attachChangeProgress;
        if (this.prevAttachDir != this.getMagneticAttachmentFace()) {
            if (attachChangeProgress < 1.0F) {
                attachChangeProgress += 0.1F;
            } else if (attachChangeProgress >= 1.0F) {
                this.prevAttachDir = this.getMagneticAttachmentFace();
            }
        } else {
            this.attachChangeProgress = 1.0F;
        }

        if (MagnetUtil.isPulledByMagnets(thisEntity)) {
            MagnetUtil.tickMagnetism(thisEntity);
            if (this.jumpFlipCooldown > 0) {
                this.jumpFlipCooldown--;
            }
        } else {
            if (this.getMagneticAttachmentFace() != Direction.DOWN) {
                this.setMagneticAttachmentFace(Direction.DOWN);
                this.refreshDimensions();
            }
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;getEyePosition()Lnet/minecraft/world/phys/Vec3;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_getEyePosition(CallbackInfoReturnable<Vec3> cir) {
        if (getMagneticAttachmentFace() != Direction.DOWN) {
            cir.setReturnValue(MagnetUtil.getEyePositionForAttachment((Entity) (Object) this, getMagneticAttachmentFace(), 1.0F));
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;getEyePosition(F)Lnet/minecraft/world/phys/Vec3;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_getEyePosition_lerp(float partialTick, CallbackInfoReturnable<Vec3> cir) {
        if (getMagneticAttachmentFace() != Direction.DOWN && getMagneticAttachmentFace() != Direction.UP) {
            cir.setReturnValue(MagnetUtil.getEyePositionForAttachment((Entity) (Object) this, getMagneticAttachmentFace(), partialTick));
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    //must override entire method for compatibility with Radium mod
    public void ac_collide(Vec3 deltaIn, CallbackInfoReturnable<Vec3> cir) {
        Entity thisEntity = (Entity) (Object) this;
        // On Fabric, several AC/Citadel entities rely on custom block collision but cannot safely
        // depend on Entity.collide() being directly overridable.
        if (thisEntity instanceof ICustomCollisions) {
            cir.setReturnValue(ICustomCollisions.getAllowedMovementForEntity(thisEntity, deltaIn));
            return;
        }

        AABB aabb = this.getBoundingBox();
        //AC CODE START
        List<VoxelShape> list;
        //fix infinity voxel collection crash for ItemEntity
        if (this.getY() > this.level().getMinBuildHeight() - 200) {
            list = this.level().getEntityCollisions(thisEntity, aabb.expandTowards(deltaIn));
            List<VoxelShape> list2 = MagnetUtil.getMovingBlockCollisions(thisEntity, aabb);
            list = ImmutableList.<VoxelShape>builder().addAll(list).addAll(list2).build();
        } else {
            list = List.of();
        }
        //AC CODE END
        Vec3 vec3 = deltaIn.lengthSqr() == 0.0D ? deltaIn : Entity.collideBoundingBox(thisEntity, deltaIn, aabb, this.level(), list);
        boolean flag = deltaIn.x != vec3.x;
        boolean flag1 = deltaIn.y != vec3.y;
        boolean flag2 = deltaIn.z != vec3.z;
        boolean flag3 = this.onGround() || flag1 && deltaIn.y < 0.0D;
        float stepHeight = thisEntity.maxUpStep();
        if (stepHeight > 0.0F && flag3 && (flag || flag2)) {
            Vec3 vec31 = Entity.collideBoundingBox(thisEntity, new Vec3(deltaIn.x, stepHeight, deltaIn.z), aabb, this.level, list);
            Vec3 vec32 = Entity.collideBoundingBox(thisEntity, new Vec3(0.0D, stepHeight, 0.0D), aabb.expandTowards(deltaIn.x, 0.0D, deltaIn.z), this.level, list);
            if (vec32.y < (double) stepHeight) {
                Vec3 vec33 = Entity.collideBoundingBox(thisEntity, new Vec3(deltaIn.x, 0.0D, deltaIn.z), aabb.move(vec32), this.level(), list).add(vec32);
                if (vec33.horizontalDistanceSqr() > vec31.horizontalDistanceSqr()) {
                    vec31 = vec33;
                }
            }

            if (vec31.horizontalDistanceSqr() > vec3.horizontalDistanceSqr()) {
                cir.setReturnValue(vec31.add(Entity.collideBoundingBox(thisEntity, new Vec3(0.0D, -vec31.y + deltaIn.y, 0.0D), aabb.move(vec31), this.level(), list)));
                return;
            }
        }

        cir.setReturnValue(vec3);
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;turn(DD)V"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_turn(double yBy, double xBy, CallbackInfo ci) {
        if (getMagneticAttachmentFace() != Direction.DOWN) {
            ci.cancel();
            MagnetUtil.turnEntityOnMagnet((Entity) (Object) this, xBy, yBy, getMagneticAttachmentFace());
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;makeBoundingBox()Lnet/minecraft/world/phys/AABB;"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_makeBoundingBox(CallbackInfoReturnable<AABB> cir) {
        if (this.entityData.isDirty() && getMagneticAttachmentFace() != Direction.DOWN) {
            cir.setReturnValue(MagnetUtil.rotateBoundingBox(dimensions, getMagneticAttachmentFace(), position));
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;isInWater()Z"},
            remap = true,
            cancellable = true,
            at = @At(value = "HEAD")
    )
    public void ac_isInWater(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof LivingEntity living && living.getActiveEffectsMap() != null && living.hasEffect(ACEffectRegistry.BUBBLED) && living.canBreatheUnderwater() && !living.getType().is(ACTagRegistry.RESISTS_BUBBLED)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = {"Lnet/minecraft/world/entity/Entity;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"},
            remap = true,
            cancellable = true,
            at = {@At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;updateEntityAfterFallOn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;)V",
                    shift = At.Shift.AFTER
            )}
    )
    public void ac_move(MoverType moverType, Vec3 vec3, CallbackInfo ci) {
        if ((Object) this instanceof LivingEntity living && living.getItemBySlot(EquipmentSlot.FEET).is(ACItemRegistry.RAINBOUNCE_BOOTS.get())) {
            RainbounceBootsItem.onEntityLand(living, vec3);
        }
    }

    @Override
    public float getMagneticDeltaX() {
        MagneticEntityData data = getMagneticData();
        return data != null ? data.getDeltaX() : 0F;
    }

    @Override
    public float getMagneticDeltaY() {
        MagneticEntityData data = getMagneticData();
        return data != null ? data.getDeltaY() : 0F;
    }

    @Override
    public float getMagneticDeltaZ() {
        MagneticEntityData data = getMagneticData();
        return data != null ? data.getDeltaZ() : 0F;
    }

    @Override
    public Direction getMagneticAttachmentFace() {
        MagneticEntityData data = getMagneticData();
        return data != null ? data.getAttachmentDirection() : Direction.DOWN;
    }

    @Override
    public Direction getPrevMagneticAttachmentFace() {
        return prevAttachDir;
    }

    @Override
    public float getAttachmentProgress(float partialTicks) {
        return prevAttachChangeProgress + (attachChangeProgress - prevAttachChangeProgress) * partialTicks;
    }

    @Override
    public void setMagneticDeltaX(float f) {
        MagneticEntityData data = getMagneticData();
        if (data != null) {
            data.setDeltaX(f);
            syncMagneticData();
        }
    }

    @Override
    public void setMagneticDeltaY(float f) {
        MagneticEntityData data = getMagneticData();
        if (data != null) {
            data.setDeltaY(f);
            syncMagneticData();
        }
    }

    @Override
    public void setMagneticDeltaZ(float f) {
        MagneticEntityData data = getMagneticData();
        if (data != null) {
            data.setDeltaZ(f);
            syncMagneticData();
        }
    }

    @Override
    public void setMagneticAttachmentFace(Direction dir) {
        MagneticEntityData data = getMagneticData();
        if (data != null) {
            data.setAttachmentDirection(dir);
            syncMagneticData();
        }
    }

    @Override
    public void postMagnetJump() {
        this.jumpFlipCooldown = 20;
    }

    @Override
    public boolean canChangeDirection() {
        return jumpFlipCooldown <= 0 && getAttachmentProgress(1.0F) == 1.0F;
    }

    @Override
    public void stepOnMagnetBlock(BlockPos pos) {
        if (lastStepPos == null || lastStepPos.distSqr(pos) > 2) {
            this.lastStepPos = pos;
            this.playStepSound(pos, level.getBlockState(pos));
        }
    }
}
