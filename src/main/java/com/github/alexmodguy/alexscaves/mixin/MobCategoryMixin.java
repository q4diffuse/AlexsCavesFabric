package com.github.alexmodguy.alexscaves.mixin;

import com.github.alexmodguy.alexscaves.server.entity.ACMobCategories;
import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

/**
 * Fabric has no enum extension support, so the custom spawn categories are appended to the array returned by
 * MobCategory.values(). It is first called while MobCategory's codec is built, and Enum.valueOf reads it reflectively
 * (the original NeoForge mod registered them through enumextensions.json).
 * values() is targeted because the synthetic $values() method has no stable name once remapped to intermediary.
 */
@Mixin(MobCategory.class)
public abstract class MobCategoryMixin {

    @Inject(method = "values", at = @At("RETURN"), cancellable = true, remap = false)
    private static void ac_addCustomCategories(CallbackInfoReturnable<MobCategory[]> cir) {
        MobCategory[] vanilla = cir.getReturnValue();
        if (ACMobCategories.extendedValues == null) {
            int count = vanilla.length;
            MobCategory[] extended = Arrays.copyOf(vanilla, count + 2);
            // same parameters as the original ALEXSCAVES_CAVE_CREATURE / ALEXSCAVES_DEEP_SEA_CREATURE enum extensions
            ACMobCategories.caveCreature = MobCategoryInvoker.ac_create("ALEXSCAVES_CAVE_CREATURE", count, "alexscaves:cave_creature", 10, true, true, 128);
            ACMobCategories.deepSeaCreature = MobCategoryInvoker.ac_create("ALEXSCAVES_DEEP_SEA_CREATURE", count + 1, "alexscaves:deep_sea_creature", 20, true, false, 128);
            extended[count] = ACMobCategories.caveCreature;
            extended[count + 1] = ACMobCategories.deepSeaCreature;
            ACMobCategories.extendedValues = extended;
        }
        cir.setReturnValue(ACMobCategories.extendedValues.clone());
    }
}
