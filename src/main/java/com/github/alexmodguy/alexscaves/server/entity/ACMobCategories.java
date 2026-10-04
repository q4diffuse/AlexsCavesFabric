package com.github.alexmodguy.alexscaves.server.entity;

import net.minecraft.world.entity.MobCategory;

/**
 * Holder for the custom spawn categories appended to MobCategory by MobCategoryMixin.
 */
public final class ACMobCategories {
    public static MobCategory caveCreature;
    public static MobCategory deepSeaCreature;
    public static MobCategory[] extendedValues;

    private ACMobCategories() {
    }

    public static MobCategory caveCreature() {
        ensureLoaded();
        return caveCreature;
    }

    public static MobCategory deepSeaCreature() {
        ensureLoaded();
        return deepSeaCreature;
    }

    private static void ensureLoaded() {
        if (caveCreature == null) {
            // initializing MobCategory runs the mixin, which fills the fields above
            MobCategory.values();
        }
    }
}
