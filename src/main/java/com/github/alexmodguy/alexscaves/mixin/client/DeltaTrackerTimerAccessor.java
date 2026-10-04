package com.github.alexmodguy.alexscaves.mixin.client;

import net.minecraft.client.DeltaTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

// Citadel's access transformer made this field writable to slow the client down (Sugar Rush)
@Mixin(DeltaTracker.Timer.class)
public interface DeltaTrackerTimerAccessor {
    @Mutable
    @Accessor("msPerTick")
    void ac_setMsPerTick(float msPerTick);
}
