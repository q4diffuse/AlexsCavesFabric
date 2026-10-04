package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.misc.ACRuntimeData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Evoker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// In 1.21, only Evoker overrides isAlliedTo. Pillager, Vindicator, and Illusioner inherit from Entity directly.
// This mixin now only targets Evoker since the other classes don't override isAlliedTo.
@Mixin(Evoker.class)
public abstract class IllagerMixin {

    @Inject(
            method = "isAlliedTo(Lnet/minecraft/world/entity/Entity;)Z",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = true
    )
    private void ac_isAlliedTo(Entity other, CallbackInfoReturnable<Boolean> cir) {
        if (isPossessed((Entity) (Object) this) || (other != null && isPossessed(other))) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static boolean isPossessed(Entity e) {
        return ACRuntimeData.read(e).getBoolean("TotemPossessed");
    }
}
