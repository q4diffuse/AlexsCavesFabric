package com.github.alexmodguy.alexscaves;

import com.github.alexmodguy.alexscaves.client.config.ACClientConfig;
import com.github.alexmodguy.alexscaves.config.ACModConfigSpec;
import com.github.alexmodguy.alexscaves.server.CommonProxy;
import com.github.alexmodguy.alexscaves.server.config.ACServerConfig;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.surface.ACSurfaceRules;
import com.github.alexmodguy.alexscaves.server.misc.ACLoadedMods;
import com.github.alexmodguy.alexscaves.server.misc.WebHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlexsCaves {
    public static final String MODID = "alexscaves";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String VERSION = "2.0.2-6-fabric";

    public static CommonProxy PROXY = new CommonProxy();

    public static final ACServerConfig COMMON_CONFIG;
    public static final ACClientConfig CLIENT_CONFIG;
    public static final List<String> MOD_GENERATION_CONFLICTS = new ArrayList<>();
    private static boolean worldgenBootstrapped;

    static {
        final Pair<ACServerConfig, ACModConfigSpec> serverPair = new ACModConfigSpec.Builder()
            .configure(ACServerConfig::new);
        COMMON_CONFIG = serverPair.getLeft();

        final Pair<ACClientConfig, ACModConfigSpec> clientPair = new ACModConfigSpec.Builder()
            .configure(ACClientConfig::new);
        CLIENT_CONFIG = clientPair.getLeft();
    }

    public static void setProxy(CommonProxy proxy) {
        PROXY = proxy;
    }

    public static void init() {
        BiomeGenerationConfig.reloadConfig();
        bootstrapWorldgen();
        ACLoadedMods.afterAllModsLoaded();
        PROXY.initPathfinding();
        readModIncompatibilities();
    }

    public static void initClient() {
        PROXY.commonInit(null);
        PROXY.clientInit(null);
    }

    public static <MSG extends CustomPacketPayload> void sendMSGToServer(MSG message) {
        PacketDistributor.sendToServer(message);
    }

    public static <MSG extends CustomPacketPayload> void sendMSGToAll(MSG message) {
        if (ServerLifecycleHooks.getCurrentServer() == null) {
            return;
        }
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            sendNonLocal(message, player);
        }
    }

    public static <MSG extends CustomPacketPayload> void sendNonLocal(MSG msg, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, msg);
    }

    private static void readModIncompatibilities() {
        MOD_GENERATION_CONFLICTS.clear();
        BufferedReader urlContents = WebHelper.getURLContents(
            "https://raw.githubusercontent.com/AlexModGuy/AlexsCaves/main/src/main/resources/assets/alexscaves/warning/mod_generation_conflicts.txt",
            "assets/alexscaves/warning/mod_generation_conflicts.txt"
        );
        if (urlContents == null) {
            LOGGER.warn("Failed to load mod conflicts");
            return;
        }

        try {
            String line;
            while ((line = urlContents.readLine()) != null) {
                MOD_GENERATION_CONFLICTS.add(line);
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to load mod conflicts", e);
        }
    }

    private static void bootstrapWorldgen() {
        if (worldgenBootstrapped) {
            return;
        }
        worldgenBootstrapped = true;
        ACBiomeRegistry.init();
        ACSurfaceRules.setup();
    }
}
