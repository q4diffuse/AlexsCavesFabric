package com.github.alexthe666.citadel;

import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Citadel {
    public static final Logger LOGGER = LogManager.getLogger("citadel");
    public static final Proxy PROXY = new Proxy();

    public static class Proxy {
        public void handleAnimationPacket(int entityId, int index) {
        }

        public void handleClientTickRatePacket(CompoundTag compoundTag) {
        }
    }
}
