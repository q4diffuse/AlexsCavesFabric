package com.github.alexmodguy.alexscaves.mixin.client;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
    @Accessor("effectActive")
    boolean isEffectActive();

    @Invoker("loadEffect")
    void invokeLoadEffect(ResourceLocation resourceLocation);
}
