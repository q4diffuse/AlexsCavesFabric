package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.entity.living.CorrodentEntity;
import com.github.alexmodguy.alexscaves.citadel.server.entity.collision.ICustomCollisions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to replicate 1.20 Corrodent collision logic:
 * - When NOT digging: use vanilla collision (super.collide)
 * - When digging: use ICustomCollisions.getAllowedMovementForEntity
 */
@Mixin(value = Entity.class, priority = 900)
public abstract class CorrodentCollisionMixin {

    @Inject(method = "collide", at = @At("HEAD"), cancellable = true)
    private void alexscaves_corrodentCollide(Vec3 vec, CallbackInfoReturnable<Vec3> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof CorrodentEntity corrodent) {
            if (corrodent.isDigging()) {
                // 1.20 logic: when digging, use custom collision
                cir.setReturnValue(ICustomCollisions.getAllowedMovementForEntity(self, vec));
            }
            // when not digging, don't cancel - use vanilla collision (like super.collide in 1.20)
        }
    }
}
