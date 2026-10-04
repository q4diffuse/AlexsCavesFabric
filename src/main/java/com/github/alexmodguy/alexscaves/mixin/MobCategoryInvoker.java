package com.github.alexmodguy.alexscaves.mixin;

import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MobCategory.class)
public interface MobCategoryInvoker {

    @Invoker("<init>")
    static MobCategory ac_create(String enumName, int ordinal, String name, int max, boolean friendly, boolean persistent, int despawnDistance) {
        throw new AssertionError();
    }
}
