package com.github.alexmodguy.alexscaves.server.entity.util;

public interface FlyingMount {

    /**
     * Stand-in for NeoForge's IEntityExtension#shouldRiderSit, read by LivingEntityRendererMixin.
     */
    default boolean shouldRiderSit() {
        return true;
    }
}
