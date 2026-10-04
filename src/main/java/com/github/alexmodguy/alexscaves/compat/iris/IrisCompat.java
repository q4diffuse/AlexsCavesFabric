package com.github.alexmodguy.alexscaves.compat.iris;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import net.fabricmc.loader.api.FabricLoader;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Optional hooks into Iris' public API, looked up reflectively so Iris stays an optional dependency.
 */
public final class IrisCompat {

    @Nullable
    private static final MethodHandle IS_SHADER_PACK_IN_USE = findApiMethod("isShaderPackInUse");
    @Nullable
    private static final MethodHandle IS_RENDERING_SHADOW_PASS = findApiMethod("isRenderingShadowPass");

    private IrisCompat() {
    }

    public static boolean isShaderPackInUse() {
        return invoke(IS_SHADER_PACK_IN_USE);
    }

    public static boolean isRenderingShadowPass() {
        return invoke(IS_RENDERING_SHADOW_PASS);
    }

    private static boolean invoke(@Nullable MethodHandle handle) {
        if (handle == null) {
            return false;
        }
        try {
            return (boolean) handle.invokeExact();
        } catch (Throwable throwable) {
            return false;
        }
    }

    @Nullable
    private static MethodHandle findApiMethod(String name) {
        if (!FabricLoader.getInstance().isModLoaded("iris")) {
            return null;
        }
        try {
            Class<?> apiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            Object api = apiClass.getMethod("getInstance").invoke(null);
            return MethodHandles.publicLookup()
                    .findVirtual(apiClass, name, MethodType.methodType(boolean.class))
                    .bindTo(api);
        } catch (ReflectiveOperationException exception) {
            AlexsCaves.LOGGER.warn("Could not hook Iris API method {}", name, exception);
            return null;
        }
    }
}
