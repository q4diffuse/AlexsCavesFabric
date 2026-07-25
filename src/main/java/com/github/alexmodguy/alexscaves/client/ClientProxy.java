package com.github.alexmodguy.alexscaves.client;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.event.ClientEvents;
import com.github.alexmodguy.alexscaves.client.gui.NuclearFurnaceScreen;
import com.github.alexmodguy.alexscaves.client.gui.SpelunkeryTableScreen;
import com.github.alexmodguy.alexscaves.client.gui.book.CaveBookScreen;
import com.github.alexmodguy.alexscaves.client.model.baked.BakedModelShadeLayerFullbright;
import com.github.alexmodguy.alexscaves.client.particle.*;
import com.github.alexmodguy.alexscaves.client.render.ACInternalShaders;
import com.github.alexmodguy.alexscaves.client.render.ACBlockRenderLayerRegistry;
import com.github.alexmodguy.alexscaves.client.render.blockentity.*;
import com.github.alexmodguy.alexscaves.client.render.entity.*;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.ClientLayerRegistry;
import com.github.alexmodguy.alexscaves.client.render.item.ACArmorRenderProperties;
import com.github.alexmodguy.alexscaves.client.render.item.ACItemRenderProperties;
import com.github.alexmodguy.alexscaves.client.render.item.RaygunRenderHelper;
import com.github.alexmodguy.alexscaves.client.render.item.tooltip.ClientSackOfSatingTooltip;
import com.github.alexmodguy.alexscaves.client.sound.*;
import com.github.alexmodguy.alexscaves.mixin.client.SoundEngineAccessor;
import com.github.alexmodguy.alexscaves.mixin.client.SoundManagerAccessor;
import com.github.alexmodguy.alexscaves.server.CommonProxy;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.AcidBlock;
import com.github.alexmodguy.alexscaves.server.block.ActivatedByAltar;
import com.github.alexmodguy.alexscaves.server.block.FrostedChocolateBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.*;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.BeholderEyeEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.QuarrySmasherEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import com.github.alexmodguy.alexscaves.server.inventory.ACMenuRegistry;
import com.github.alexmodguy.alexscaves.server.item.*;
import com.github.alexmodguy.alexscaves.server.item.tooltip.SackOfSatingTooltip;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.BiomeSampler;
import com.github.alexmodguy.alexscaves.server.misc.ACKeybindRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.citadel.client.shader.PostEffectRegistry;
import com.github.alexmodguy.alexscaves.citadel.client.tick.ClientTickRateTracker;
import com.github.alexmodguy.alexscaves.citadel.server.tick.ServerTickRateTracker;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModContainer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class ClientProxy extends CommonProxy {

    private static final List<String> FULLBRIGHTS = ImmutableList.of("alexscaves:ambersol#",
            "alexscaves:radrock_uranium_ore#", "alexscaves:acidic_radrock#", "alexscaves:uranium_rod#axis=x",
            "alexscaves:uranium_rod#axis=y", "alexscaves:uranium_rod#axis=z", "alexscaves:block_of_uranium#",
            "alexscaves:abyssal_altar#active=true", "alexscaves:abyssmarine_", "alexscaves:peering_coprolith#",
            "alexscaves:forsaken_idol#", "alexscaves:magnetic_light#", "alexscaves:tremorzilla_egg#");
    public static final ResourceLocation BOMB_FLASH = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,
            "textures/misc/bomb_flash.png");
    public static final ResourceLocation WATCHER_EFFECT = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,
            "textures/misc/watcher_effect.png");
    public static final ResourceLocation IRRADIATED_SHADER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,
            "shaders/post/irradiated.json");
    public static final ResourceLocation HOLOGRAM_SHADER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,
            "shaders/post/hologram.json");
    public static final ResourceLocation PURPLE_WITCH_SHADER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,
            "shaders/post/purple_witch.json");
    public static final ResourceLocation SUGAR_RUSH_SHADER = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID,
            "shaders/post/sugar_rush.json");
    public static final RandomSource random = RandomSource.create();
    public static int lastTremorTick = -1;
    public static float[] randomTremorOffsets = new float[3];
    public static List<UUID> blockedEntityRenders = new ArrayList<>();
    public static Map<ClientLevel, List<BlockPos>> blockedParticleLocations = new HashMap<>();
    public static Map<LivingEntity, Vec3[]> darknessTrailPosMap = new HashMap<>();
    public static Map<LivingEntity, Integer> darknessTrailPointerMap = new HashMap<>();
    public static int muteNonNukeSoundsFor = 0;
    public static int renderNukeFlashFor = 0;
    public static boolean primordialBossActive = false;
    public static float prevPrimordialBossActiveAmount = 0;
    public static float primordialBossActiveAmount = 0;
    public static ClientLevel lastBossLevel;
    public static float prevNukeFlashAmount = 0;
    public static float nukeFlashAmount = 0;
    public static float prevPossessionStrengthAmount = 0;
    public static float possessionStrengthAmount = 0;
    public static int renderNukeSkyDarkFor = 0;
    public static int renderNukeShakeFor = 0;
    public static float masterVolumeNukeModifier = 0.0F;
    // Client-side tracking for bubbled effect visuals (entity ID -> remaining ticks)
    private static final it.unimi.dsi.fastutil.ints.Int2IntMap BUBBLED_EFFECT_TICKS = new it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap();
    public static final Int2ObjectMap<AbstractTickableSoundInstance> ENTITY_SOUND_INSTANCE_MAP = new Int2ObjectOpenHashMap<>();
    public static final Map<BlockEntity, AbstractTickableSoundInstance> BLOCK_ENTITY_SOUND_INSTANCE_MAP = new HashMap<>();
    private final ACItemRenderProperties isterProperties = new ACItemRenderProperties();
    private final ACArmorRenderProperties armorProperties = new ACArmorRenderProperties();
    @FunctionalInterface
    private interface ShaderRegistrar {
        void register(ResourceLocation id, VertexFormat vertexFormat, Consumer<ShaderInstance> consumer)
                throws IOException;
    }
    public static boolean spelunkeryTutorialComplete;
    public static boolean hasACSplashText = false;
    public static CameraType lastPOV = CameraType.FIRST_PERSON;
    public static int shaderLoadAttemptCooldown = 0;
    public static Vec3 lastBiomeLightColor = new Vec3(1.0D, 1.0D, 1.0D);
    public static float lastBiomeAmbientLightAmount = 0;
    public static Vec3 lastBiomeLightColorPrev = new Vec3(1.0D, 1.0D, 1.0D);
    public static float lastBiomeAmbientLightAmountPrev = 0;
    public static Map<UUID, Integer> bossBarRenderTypes = new HashMap<>();
    private static Entity lastCameraEntity;
    public static float acSkyOverrideAmount;
    public static Vec3 acSkyOverrideColor = Vec3.ZERO;
    public static boolean disabledBiomeAmbientLightByOtherMod = false;
    private static float lastSampledFogNearness = 1.0F;
    private static float lastSampledWaterFogFarness = 1.0F;
    private static Vec3 lastSampledFogColor = Vec3.ZERO;
    private static Vec3 lastSampledWaterFogColor = Vec3.ZERO;
    private static final com.github.alexmodguy.alexscaves.client.render.item.ACItemstackRenderer FABRIC_ITEM_RENDERER = new com.github.alexmodguy.alexscaves.client.render.item.ACItemstackRenderer();

    /**
     * Ticks down all bubbled effect timers. Called from ClientEvents.
     */
    public static void tickBubbledEffects() {
        if (BUBBLED_EFFECT_TICKS.isEmpty()) return;
        var iterator = BUBBLED_EFFECT_TICKS.int2IntEntrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            int newValue = entry.getIntValue() - 1;
            if (newValue <= 0) {
                iterator.remove();
            } else {
                entry.setValue(newValue);
            }
        }
    }

    public static void registerFabricShaders() {
        CoreShaderRegistrationCallback.EVENT.register(context -> registerInternalShaders(context::register));
    }

    public static void registerFabricClientLifecycle() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientProxy::onFabricClientTick);
        WorldRenderEvents.BEFORE_ENTITIES.register(ClientProxy::onFabricBeforeEntities);
        WorldRenderEvents.AFTER_ENTITIES.register(ClientProxy::onFabricAfterEntities);
    }

    public static void registerFabricBuiltinItemRenderers() {
        registerBuiltinItemRenderer(ACItemRegistry.CAVE_MAP.get());
        registerBuiltinItemRenderer(ACItemRegistry.DREADBOW.get());
        registerBuiltinItemRenderer(ACItemRegistry.RAYGUN.get());
        registerBuiltinItemRenderer(ACItemRegistry.PRIMITIVE_CLUB.get());
        registerBuiltinItemRenderer(ACItemRegistry.LIMESTONE_SPEAR.get());
        registerBuiltinItemRenderer(ACItemRegistry.EXTINCTION_SPEAR.get());
        registerBuiltinItemRenderer(ACItemRegistry.FROSTMINT_SPEAR.get());
        registerBuiltinItemRenderer(ACItemRegistry.SEA_STAFF.get());
        registerBuiltinItemRenderer(ACItemRegistry.ORTHOLANCE.get());
        registerBuiltinItemRenderer(ACItemRegistry.GALENA_GAUNTLET.get());
        registerBuiltinItemRenderer(ACItemRegistry.RESISTOR_SHIELD.get());
        registerBuiltinItemRenderer(ACItemRegistry.SHOT_GUM.get());
        registerBuiltinItemRenderer(ACItemRegistry.SUGAR_STAFF.get());
        registerBuiltinItemRenderer(ACBlockRegistry.SIREN_LIGHT.get());
        registerBuiltinItemRenderer(ACBlockRegistry.COPPER_VALVE.get());
        registerBuiltinItemRenderer(ACBlockRegistry.BEHOLDER.get());
        registerBuiltinItemRenderer(ACBlockRegistry.GOBTHUMPER.get());
    }

    public static void registerFabricFluidRendering() {
        FluidRenderHandlerRegistry.INSTANCE.register(
                ACFluidRegistry.ACID_FLUID_SOURCE.get(),
                ACFluidRegistry.ACID_FLUID_FLOWING.get(),
                new SimpleFluidRenderHandler(
                        com.github.alexmodguy.alexscaves.server.block.fluid.AcidFluidType.FLUID_STILL,
                        com.github.alexmodguy.alexscaves.server.block.fluid.AcidFluidType.FLUID_FLOWING
                )
        );
        FluidRenderHandlerRegistry.INSTANCE.register(
                ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE.get(),
                ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING.get(),
                new SimpleFluidRenderHandler(
                        com.github.alexmodguy.alexscaves.server.block.fluid.PurpleSodaFluidType.FLUID_STILL,
                        com.github.alexmodguy.alexscaves.server.block.fluid.PurpleSodaFluidType.FLUID_FLOWING
                )
        );
    }

    private static void registerBuiltinItemRenderer(net.minecraft.world.level.ItemLike itemLike) {
        BuiltinItemRendererRegistry.INSTANCE.register(itemLike.asItem(), (stack, mode, matrices, vertexConsumers, light, overlay) ->
                FABRIC_ITEM_RENDERER.renderByItem(stack, mode, matrices, vertexConsumers, light, overlay));
    }

    private static void onFabricClientTick(Minecraft minecraft) {
        Entity cameraEntity = minecraft.cameraEntity;
        tickBubbledEffects();
        if (shaderLoadAttemptCooldown > 0) {
            shaderLoadAttemptCooldown--;
        }
        if (renderNukeSkyDarkFor > 0) {
            renderNukeSkyDarkFor--;
        }
        if (renderNukeShakeFor > 0) {
            renderNukeShakeFor--;
        }
        if (renderNukeFlashFor > 0) {
            renderNukeFlashFor--;
        }
        if (muteNonNukeSoundsFor > 0) {
            muteNonNukeSoundsFor--;
        }
        prevNukeFlashAmount = nukeFlashAmount;
        nukeFlashAmount = Mth.approach(nukeFlashAmount, renderNukeFlashFor > 0 ? 1.0F : 0.0F, renderNukeFlashFor > 0 ? 0.25F : 0.08F);
        masterVolumeNukeModifier = Mth.approach(masterVolumeNukeModifier, muteNonNukeSoundsFor > 0 ? 1.0F : 0.0F, 0.08F);
        if (cameraEntity == null || minecraft.level == null) {
            acSkyOverrideAmount = 0.0F;
            acSkyOverrideColor = Vec3.ZERO;
            lastBiomeLightColorPrev = lastBiomeLightColor;
            lastBiomeLightColor = new Vec3(1.0D, 1.0D, 1.0D);
            lastBiomeAmbientLightAmountPrev = lastBiomeAmbientLightAmount;
            lastBiomeAmbientLightAmount = 0.0F;
            lastSampledFogNearness = 1.0F;
            lastSampledWaterFogFarness = 1.0F;
            lastSampledFogColor = Vec3.ZERO;
            lastSampledWaterFogColor = Vec3.ZERO;
            return;
        }
        acSkyOverrideAmount = ACBiomeRegistry.calculateBiomeSkyOverride(cameraEntity);
        if (acSkyOverrideAmount > 0.0F) {
            acSkyOverrideColor = BiomeSampler.sampleBiomesVec3(
                    minecraft.level,
                    cameraEntity.position(),
                    biomeHolder -> Vec3.fromRGB24(biomeHolder.value().getSkyColor())
            );
        } else {
            acSkyOverrideColor = Vec3.ZERO;
        }
        lastBiomeLightColorPrev = lastBiomeLightColor;
        lastBiomeLightColor = calculateBiomeLightColor(cameraEntity);
        lastBiomeAmbientLightAmountPrev = lastBiomeAmbientLightAmount;
        lastBiomeAmbientLightAmount = calculateBiomeAmbientLight(cameraEntity);
        lastSampledFogNearness = calculateBiomeFogNearness(cameraEntity);
        lastSampledWaterFogFarness = calculateBiomeWaterFogFarness(cameraEntity);
        if (cameraEntity.level() instanceof ClientLevel) {
            lastSampledFogColor = calculateBiomeFogColor(cameraEntity);
            lastSampledWaterFogColor = calculateBiomeWaterFogColor(cameraEntity);
        } else {
            lastSampledFogColor = Vec3.ZERO;
            lastSampledWaterFogColor = Vec3.ZERO;
        }
    }

    private static void onFabricBeforeEntities(WorldRenderContext context) {
        renderFirstPersonRaygunRays(context, 2);
    }

    private static void onFabricAfterEntities(WorldRenderContext context) {
        renderFirstPersonRaygunRays(context, 1);
    }

    private static void renderFirstPersonRaygunRays(WorldRenderContext context, int firstPersonPass) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!minecraft.options.getCameraType().isFirstPerson() || context.matrixStack() == null || context.consumers() == null) {
            return;
        }
        if (!(minecraft.getCameraEntity() instanceof LivingEntity living)) {
            return;
        }
        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(true);
        RaygunRenderHelper.renderRaysFor(living, minecraft.gameRenderer.getMainCamera().getPosition(), context.matrixStack(), context.consumers(), partialTick, true, firstPersonPass);
    }

    private static float calculateBiomeAmbientLight(Entity player) {
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (blendRadius == 0) {
            return ACBiomeRegistry.getBiomeAmbientLight(player.level().getBiome(player.blockPosition()));
        }
        return BiomeSampler.sampleBiomesFloat(player.level(), player.position(), ACBiomeRegistry::getBiomeAmbientLight);
    }

    private static Vec3 calculateBiomeLightColor(Entity player) {
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (blendRadius == 0) {
            return ACBiomeRegistry.getBiomeLightColorOverride(player.level().getBiome(player.blockPosition()));
        }
        return BiomeSampler.sampleBiomesVec3(player.level(), player.position(), ACBiomeRegistry::getBiomeLightColorOverride);
    }

    private static float calculateBiomeFogNearness(Entity player) {
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (blendRadius == 0) {
            return ACBiomeRegistry.getBiomeFogNearness(player.level().getBiome(player.blockPosition()));
        }
        return BiomeSampler.sampleBiomesFloat(player.level(), player.position(), ACBiomeRegistry::getBiomeFogNearness);
    }

    private static float calculateBiomeWaterFogFarness(Entity player) {
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (blendRadius == 0) {
            return ACBiomeRegistry.getBiomeWaterFogFarness(player.level().getBiome(player.blockPosition()));
        }
        return BiomeSampler.sampleBiomesFloat(player.level(), player.position(), ACBiomeRegistry::getBiomeWaterFogFarness);
    }

    private static Vec3 calculateBiomeFogColor(Entity player) {
        ClientLevel level = (ClientLevel) player.level();
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (blendRadius == 0) {
            return level.effects().getBrightnessDependentFogColor(
                    Vec3.fromRGB24(level.getBiomeManager().getNoiseBiomeAtPosition(player.blockPosition()).value().getFogColor()),
                    1.0F
            );
        }
        return level.effects().getBrightnessDependentFogColor(
                BiomeSampler.sampleBiomesVec3(player.level(), player.position(), biomeHolder -> Vec3.fromRGB24(biomeHolder.value().getFogColor())),
                1.0F
        );
    }

    private static Vec3 calculateBiomeWaterFogColor(Entity player) {
        ClientLevel level = (ClientLevel) player.level();
        int blendRadius = Minecraft.getInstance().options.biomeBlendRadius().get();
        if (blendRadius == 0) {
            return level.effects().getBrightnessDependentFogColor(
                    Vec3.fromRGB24(level.getBiomeManager().getNoiseBiomeAtPosition(player.blockPosition()).value().getWaterFogColor()),
                    1.0F
            );
        }
        return level.effects().getBrightnessDependentFogColor(
                BiomeSampler.sampleBiomesVec3(player.level(), player.position(), biomeHolder -> Vec3.fromRGB24(biomeHolder.value().getWaterFogColor())),
                1.0F
        );
    }

    public static float getLastSampledFogNearness() {
        return lastSampledFogNearness;
    }

    public static float getLastSampledWaterFogFarness() {
        return lastSampledWaterFogFarness;
    }

    public static Vec3 getLastSampledFogColor() {
        return lastSampledFogColor;
    }

    public static Vec3 getLastSampledWaterFogColor() {
        return lastSampledWaterFogColor;
    }

    @SuppressWarnings("removal")
    @Override
    public void commonInit(IEventBus modEventBus) {
        if (modEventBus == null) {
            this.setupParticles(new RegisterParticleProvidersEvent());
            this.registerKeybinds(new RegisterKeyMappingsEvent());
            this.onItemColors(new RegisterColorHandlersEvent.Item());
            this.onBlockColors(new RegisterColorHandlersEvent.Block());
            this.onRegisterTooltips(new RegisterClientTooltipComponentFactoriesEvent());
            this.onRegisterMenuScreens(new RegisterMenuScreensEvent());
            return;
        }
        modEventBus.addListener(this::setupParticles);
        modEventBus.addListener(this::registerKeybinds);
        modEventBus.addListener(this::onItemColors);
        modEventBus.addListener(this::onBlockColors);
        modEventBus.addListener(this::onRegisterTooltips);
        modEventBus.addListener(this::onRegisterMenuScreens);
    }

    public void onRegisterMenuScreens(net.neoforged.neoforge.client.event.RegisterMenuScreensEvent event) {
        event.register(ACMenuRegistry.SPELUNKERY_TABLE_MENU.get(), SpelunkeryTableScreen::new);
        event.register(ACMenuRegistry.NUCLEAR_FURNACE_MENU.get(), NuclearFurnaceScreen::new);
    }

    @SuppressWarnings("removal")
    @Override
    public void clientInit(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.register(new ClientEvents());
        if (modEventBus == null) {
            ClientLayerRegistry.addLayers(new EntityRenderersEvent.AddLayers());
            this.bakeModels(new ModelEvent.ModifyBakingResult());
        } else {
            modEventBus.addListener(ClientLayerRegistry::addLayers);
            modEventBus.addListener(this::bakeModels);
            modEventBus.addListener(this::registerShaders);
        }
        EntityRendererRegistry.register(ACEntityRegistry.BOAT.get(), (context) -> {
            return new AlexsCavesBoatRenderer(context, false);
        });
        EntityRendererRegistry.register(ACEntityRegistry.CHEST_BOAT.get(), (context) -> {
            return new AlexsCavesBoatRenderer(context, true);
        });
        BlockEntityRenderers.register(ACBlockEntityRegistry.MAGNET.get(), MagnetBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.TESLA_BULB.get(), TelsaBulbBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.HOLOGRAM_PROJECTOR.get(),
                HologramProjectorBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.QUARRY.get(), QuarryBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.AMBERSOL.get(), AmbersolBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.AMBER_MONOLITH.get(), AmberMonolithBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.NUCLEAR_FURNACE.get(), NuclearFurnaceBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.SIREN_LIGHT.get(), SirenLightBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.ABYSSAL_ALTAR.get(), AbyssalAltarBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.COPPER_VALVE.get(), CopperValveBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.BEHOLDER.get(), BeholderBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.GOBTHUMPER.get(), GobthumperBlockRenderer::new);
        BlockEntityRenderers.register(ACBlockEntityRegistry.CONVERSION_CRUCIBLE.get(),
                ConversionCrucibleBlockRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.MOVING_METAL_BLOCK.get(), MovingMetalBlockRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.TELETOR.get(), TeletorRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.MAGNETIC_WEAPON.get(), MagneticWeaponRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.MAGNETRON.get(), MagnetronRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.BOUNDROID.get(), BoundroidRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.BOUNDROID_WINCH.get(), BoundroidWinchRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.FERROUSLIME.get(), FerrouslimeRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.NOTOR.get(), NotorRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.QUARRY_SMASHER.get(), QuarrySmasherRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SEEKING_ARROW.get(), SeekingArrowRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SUBTERRANODON.get(), SubterranodonRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.VALLUMRAPTOR.get(), VallumraptorRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GROTTOCERATOPS.get(), GrottoceratopsRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.TRILOCARIS.get(), TrilocarisRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.TREMORSAURUS.get(), TremorsaurusRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.RELICHEIRUS.get(), RelicheirusRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.FALLING_TREE_BLOCK.get(), FallingTreeBlockRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CRUSHED_BLOCK.get(), CrushedBlockRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.LIMESTONE_SPEAR.get(), LimestoneSpearRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.EXTINCTION_SPEAR.get(), ExtinctionSpearRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.DINOSAUR_SPIRIT.get(), DinosaurSpiritRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.LUXTRUCTOSAURUS.get(), LuxtructosaurusRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.TEPHRA.get(), TephraRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.ATLATITAN.get(), AtlatitanRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.NUCLEAR_EXPLOSION.get(), EmptyRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.NUCLEAR_BOMB.get(), NuclearBombRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.NUCLEEPER.get(), NucleeperRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.RADGILL.get(), RadgillRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.BRAINIAC.get(), BrainiacRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.THROWN_WASTE_DRUM.get(), ThrownWasteDrumEntityRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GAMMAROACH.get(), GammaroachRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.RAYCAT.get(), RaycatRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CINDER_BRICK.get(), (context) -> {
            return new ThrownItemRenderer<>(context, 1.25F, false);
        });
        EntityRendererRegistry.register(ACEntityRegistry.TREMORZILLA.get(), TremorzillaRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.LANTERNFISH.get(), LanternfishRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SEA_PIG.get(), SeaPigRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SUBMARINE.get(), SubmarineRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.HULLBREAKER.get(), HullbreakerRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GOSSAMER_WORM.get(), GossamerWormRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.TRIPODFISH.get(), TripodfishRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.DEEP_ONE.get(), DeepOneRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.INK_BOMB.get(), (context) -> {
            return new ThrownItemRenderer<>(context, 1.25F, false);
        });
        EntityRendererRegistry.register(ACEntityRegistry.DEEP_ONE_KNIGHT.get(), DeepOneKnightRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.DEEP_ONE_MAGE.get(), DeepOneMageRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.WATER_BOLT.get(), WaterBoltRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.WAVE.get(), WaveRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.MINE_GUARDIAN.get(), MineGuardianRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.MINE_GUARDIAN_ANCHOR.get(), MineGuardianAnchorRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.DEPTH_CHARGE.get(), (context) -> {
            return new ThrownItemRenderer<>(context, 1.75F, true);
        });
        EntityRendererRegistry.register(ACEntityRegistry.FLOATER.get(), FloaterRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GUANO.get(), (context) -> {
            return new ThrownItemRenderer<>(context, 1.25F, false);
        });
        EntityRendererRegistry.register(ACEntityRegistry.FALLING_GUANO.get(), FallingBlockRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GLOOMOTH.get(), GloomothRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.UNDERZEALOT.get(), UnderzealotRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.WATCHER.get(), WatcherRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CORRODENT.get(), CorrodentRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.VESPER.get(), VesperRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.FORSAKEN.get(), ForsakenRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.BEHOLDER_EYE.get(), EmptyRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.DESOLATE_DAGGER.get(), DesolateDaggerRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.BURROWING_ARROW.get(), BurrowingArrowRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.DARK_ARROW.get(), DarkArrowRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SWEETISH_FISH.get(), SweetishFishRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CANIAC.get(), CaniacRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GUMBEEPER.get(), GumbeeperRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GUMBALL.get(), GumballRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CANDICORN.get(), CandicornRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GUM_WORM.get(), GumWormRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GUM_WORM_SEGMENT.get(), GumWormSegmentRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CARAMEL_CUBE.get(), CaramelCubeRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.MELTED_CARAMEL.get(), MeltedCaramelRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GUMMY_BEAR.get(), GummyBearRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.LICOWITCH.get(), LicowitchRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SPINNING_PEPPERMINT.get(), SpinningPeppermintRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SUGAR_STAFF_HEX.get(), SugarStaffHexRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.GINGERBREAD_MAN.get(), GingerbreadManRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.FALLING_FROSTMINT.get(), FallingBlockRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.CANDY_CANE_HOOK.get(), CandyCaneHookRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.SODA_BOTTLE_ROCKET.get(), (render) -> {
            return new ThrownItemRenderer<>(render, 1.25F, true);
        });
        EntityRendererRegistry.register(ACEntityRegistry.FROSTMINT_SPEAR.get(), FrostmintSpearRenderer::new);
        EntityRendererRegistry.register(ACEntityRegistry.THROWN_ICE_CREAM_SCOOP.get(), (context) -> {
            return new ThrownItemRenderer<>(context, 1.25F, false);
        });
        ItemProperties.register(ACItemRegistry.HOLOCODER.get(), ResourceLocation.withDefaultNamespace("bound"),
                (stack, level, living, j) -> {
                    return HolocoderItem.isBound(stack) ? 1.0F : 0.0F;
                });
        ItemProperties.register(ACItemRegistry.DINOSAUR_NUGGET.get(), ResourceLocation.withDefaultNamespace("nugget"),
                (stack, level, living, j) -> {
                    return (stack.getCount() % 4) / 4F;
                });
        ItemProperties.register(ACItemRegistry.LIMESTONE_SPEAR.get(), ResourceLocation.withDefaultNamespace("throwing"),
                (stack, level, living, j) -> {
                    return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F;
                });
        ItemProperties.register(ACItemRegistry.EXTINCTION_SPEAR.get(),
                ResourceLocation.withDefaultNamespace("throwing"), (stack, level, living, j) -> {
                    return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F;
                });
        ItemProperties.register(ACItemRegistry.REMOTE_DETONATOR.get(), ResourceLocation.withDefaultNamespace("active"),
                (stack, level, living, j) -> {
                    return RemoteDetonatorItem.isActive(stack) ? 1.0F : 0.0F;
                });
        ItemProperties.register(ACItemRegistry.MAGIC_CONCH.get(), ResourceLocation.withDefaultNamespace("tooting"),
                (stack, level, living, j) -> {
                    return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F;
                });
        ItemProperties.register(ACItemRegistry.ORTHOLANCE.get(), ResourceLocation.withDefaultNamespace("charging"),
                (stack, level, living, j) -> {
                    return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F;
                });
        ItemProperties.register(ACItemRegistry.TOTEM_OF_POSSESSION.get(),
                ResourceLocation.withDefaultNamespace("totem"), (stack, level, living, j) -> {
                    return TotemOfPossessionItem.isBound(stack)
                            ? living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.5F
                            : 0.0F;
                });
        ItemProperties.register(ACItemRegistry.CANDY_CANE_HOOK.get(), ResourceLocation.withDefaultNamespace("cast"),
                (stack, level, holder, i) -> {
                    return holder != null && CandyCaneHookItem.isActive(stack) ? 1.0F : 0.0F;
                });
        ItemProperties.register(ACItemRegistry.SACK_OF_SATING.get(), ResourceLocation.withDefaultNamespace("open"),
                (stack, level, living, j) -> {
                    return level != null && SackOfSatingItem.isChewing(stack, level.getGameTime()) ? 1.0F
                            : stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).isEmpty()
                                    || living instanceof Player player && player.containerMenu != null
                                            && SackOfSatingItem.calculateWholeStackHungerValue(
                                                    player.containerMenu.getCarried(), player) > 0 ? 0.5F : 0.0F;
                });
        ItemProperties.register(ACItemRegistry.FROSTMINT_SPEAR.get(), ResourceLocation.withDefaultNamespace("throwing"),
                (stack, level, living, j) -> {
                    return living != null && living.isUsingItem() && living.getUseItem() == stack ? 1.0F : 0.0F;
                });
        blockedParticleLocations.clear();
        PostEffectRegistry.registerEffect(IRRADIATED_SHADER);
        PostEffectRegistry.registerEffect(HOLOGRAM_SHADER);
        PostEffectRegistry.registerEffect(PURPLE_WITCH_SHADER);
        PostEffectRegistry.registerEffect(SUGAR_RUSH_SHADER);
        ACBlockRenderLayerRegistry.register();
        // Menu screens are now registered via RegisterMenuScreensEvent in commonInit
        hasACSplashText = random.nextInt(300) == 0;
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.cutoutMipped(), ACFluidRegistry.ACID_FLUID_SOURCE.get(), ACFluidRegistry.ACID_FLUID_FLOWING.get());
        BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), ACFluidRegistry.PURPLE_SODA_FLUID_SOURCE.get(), ACFluidRegistry.PURPLE_SODA_FLUID_FLOWING.get());
    }

    public void setupParticles(RegisterParticleProvidersEvent registry) {
        AlexsCaves.LOGGER.debug("Registered particle factories");
        registry.registerSpecial(ACParticleRegistry.SCARLET_MAGNETIC_ORBIT.get(),
                new MagneticOrbitParticle.ScarletFactory());
        registry.registerSpecial(ACParticleRegistry.AZURE_MAGNETIC_ORBIT.get(),
                new MagneticOrbitParticle.AzureFactory());
        registry.registerSpecial(ACParticleRegistry.SCARLET_MAGNETIC_FLOW.get(),
                new MagneticFlowParticle.ScarletFactory());
        registry.registerSpecial(ACParticleRegistry.AZURE_MAGNETIC_FLOW.get(), new MagneticFlowParticle.AzureFactory());
        registry.registerSpecial(ACParticleRegistry.TESLA_BULB_LIGHTNING.get(),
                new TeslaBulbLightningParticle.Factory());
        registry.registerSpecial(ACParticleRegistry.MAGNET_LIGHTNING.get(), new MagnetLightningParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.GALENA_DEBRIS.get(), GalenaDebrisParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.MAGNETIC_CAVES_AMBIENT.get(),
                new MagneticCavesAmbientParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.FERROUSLIME.get(), FerrouslimeParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.QUARRY_BORDER_LIGHTING.get(),
                new QuarryBorderLightningParticle.Factory());
        registry.registerSpecial(ACParticleRegistry.AZURE_SHIELD_LIGHTNING.get(),
                new ResistorShieldLightningParticle.AzureFactory());
        registry.registerSpecial(ACParticleRegistry.SCARLET_SHIELD_LIGHTNING.get(),
                new ResistorShieldLightningParticle.ScarletFactory());
        registry.registerSpriteSet(ACParticleRegistry.FLY.get(), FlyParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.WATER_TREMOR.get(), WaterTremorParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.AMBER_MONOLITH.get(), AmberMonolithParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.AMBER_EXPLOSION.get(), SmallExplosionParticle.AmberFactory::new);
        registry.registerSpecial(ACParticleRegistry.DINOSAUR_TRANSFORMATION_AMBER.get(),
                new DinosaurTransformParticle.AmberFactory());
        registry.registerSpecial(ACParticleRegistry.DINOSAUR_TRANSFORMATION_TECTONIC.get(),
                new DinosaurTransformParticle.TectonicFactory());
        registry.registerSpecial(ACParticleRegistry.STUN_STAR.get(), new StunStarParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.TEPHRA.get(), TephraParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.TEPHRA_SMALL.get(), TephraParticle.SmallFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.TEPHRA_FLAME.get(), TephraParticle.FlameFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.LUXTRUCTOSAURUS_SPIT.get(),
                LuxtructosaurusSpitParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.LUXTRUCTOSAURUS_ASH.get(),
                LuxtructosaurusAshParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.HAPPINESS.get(), HappinessParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.ACID_BUBBLE.get(), AcidBubbleParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.BLACK_VENT_SMOKE.get(), VentSmokeParticle.BlackFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.WHITE_VENT_SMOKE.get(), VentSmokeParticle.WhiteFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.GREEN_VENT_SMOKE.get(), VentSmokeParticle.GreenFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.RED_VENT_SMOKE.get(), VentSmokeParticle.RedFactory::new);
        registry.registerSpecial(ACParticleRegistry.MUSHROOM_CLOUD.get(), new MushroomCloudParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.MUSHROOM_CLOUD_SMOKE.get(),
                SmallExplosionParticle.NukeFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.MUSHROOM_CLOUD_EXPLOSION.get(),
                SmallExplosionParticle.NukeFactory::new);
        registry.registerSpecial(ACParticleRegistry.PROTON.get(), new ProtonParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.FALLOUT.get(), FalloutParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.GAMMAROACH.get(), GammaroachParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.HAZMAT_BREATHE.get(), HazmatBreatheParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.BLUE_HAZMAT_BREATHE.get(),
                HazmatBreatheParticle.BlueFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.RADGILL_SPLASH.get(), RadgillSplashParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.ACID_DROP.get(), AcidDropParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.NUCLEAR_SIREN_SONAR.get(),
                NuclearSirenSonarParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.RAYGUN_EXPLOSION.get(),
                SmallExplosionParticle.RaygunFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.BLUE_RAYGUN_EXPLOSION.get(),
                SmallExplosionParticle.BlueRaygunFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.RAYGUN_BLAST.get(), RaygunBlastParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.TREMORZILLA_EXPLOSION.get(),
                SmallExplosionParticle.TremorzillaFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.TREMORZILLA_RETRO_EXPLOSION.get(),
                SmallExplosionParticle.TremorzillaRetroFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.TREMORZILLA_TECTONIC_EXPLOSION.get(),
                SmallExplosionParticle.TremorzillaTectonicFactory::new);
        registry.registerSpecial(ACParticleRegistry.TREMORZILLA_PROTON.get(), new TremorzillaProtonParticle.Factory());
        registry.registerSpecial(ACParticleRegistry.TREMORZILLA_RETRO_PROTON.get(),
                new TremorzillaProtonParticle.RetroFactory());
        registry.registerSpecial(ACParticleRegistry.TREMORZILLA_TECTONIC_PROTON.get(),
                new TremorzillaProtonParticle.TectonicFactory());
        registry.registerSpriteSet(ACParticleRegistry.TREMORZILLA_LIGHTNING.get(),
                TremorzillaLightningParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.TREMORZILLA_RETRO_LIGHTNING.get(),
                TremorzillaLightningParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.TREMORZILLA_TECTONIC_LIGHTNING.get(),
                TremorzillaLightningParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.TREMORZILLA_BLAST.get(),
                RaygunBlastParticle.TremorzillaFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.TREMORZILLA_STEAM.get(), TremorzillaSteamParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.TUBE_WORM.get(), new TubeWormParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.DEEP_ONE_MAGIC.get(), DeepOneMagicParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.WATER_FOAM.get(), WaterFoamParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.BIG_SPLASH.get(), new BigSplashParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.BIG_SPLASH_EFFECT.get(), BigSplashEffectParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.MINE_EXPLOSION.get(), SmallExplosionParticle.MineFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.BIO_POP.get(), BioPopParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.WATCHER_APPEARANCE.get(), new WatcherAppearanceParticle.Factory());
        registry.registerSpecial(ACParticleRegistry.VOID_BEING_CLOUD.get(), new VoidBeingCloudParticle.Factory());
        registry.registerSpecial(ACParticleRegistry.VOID_BEING_TENDRIL.get(), new VoidBeingTendrilParticle.Factory());
        registry.registerSpecial(ACParticleRegistry.VOID_BEING_EYE.get(), new VoidBeingEyeParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.UNDERZEALOT_MAGIC.get(), UnderzealotMagicParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.UNDERZEALOT_EXPLOSION.get(),
                SmallExplosionParticle.UnderzealotFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.FALLING_GUANO.get(), FallingGuanoParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.MOTH_DUST.get(), MothDustParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.FORSAKEN_SPIT.get(), ForsakenSpitParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.FORSAKEN_SONAR.get(), ForsakenSonarParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.FORSAKEN_SONAR_LARGE.get(),
                ForsakenSonarParticle.LargeFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.TOTEM_EXPLOSION.get(), SmallExplosionParticle.TotemFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.ICE_CREAM_DRIP.get(), IceCreamDripParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.ICE_CREAM_SPLASH.get(), IceCreamSplashParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.PURPLE_SODA_BUBBLE.get(), PurpleSodaBubbleParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.PURPLE_SODA_BUBBLE_EMITTER.get(),
                PurpleSodaBubbleEmitterParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.PURPLE_SODA_FIZZ.get(), PurpleSodaFizzParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.SUNDROP.get(), SundropParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.RAINBOW.get(), new RainbowParticle.Factory());
        registry.registerSpecial(ACParticleRegistry.PLAYER_RAINBOW.get(), new PlayerRainbowParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.CANDICORN_CHARGE.get(), CandicornChargeParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.BIG_BLOCK_DUST.get(), BigBlockDustParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.CARAMEL_DROP.get(), CaramelDropParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.JELLY_BEAN_EAT.get(), new JellyBeanEatParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.SLEEP.get(), SleepParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.WITCH_COOKIE.get(), WitchCookieParticle.Factory::new);
        registry.registerSpecial(ACParticleRegistry.PURPLE_WITCH_MAGIC.get(), new PurpleWitchMagicParticle.Factory());
        registry.registerSpriteSet(ACParticleRegistry.PURPLE_WITCH_EXPLOSION.get(),
                SmallExplosionParticle.PurpleWitchFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.GOBTHUMPER.get(), GobthumperParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.COLORED_DUST.get(), ColoredDustParticle.Factory::new);
        registry.registerSpriteSet(ACParticleRegistry.SMALL_COLORED_DUST.get(), ColoredDustParticle.SmallFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.CONVERSION_CRUCIBLE_EXPLOSION.get(),
                SmallExplosionParticle.ConversionCrucibleFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.FROSTMINT_EXPLOSION.get(),
                SmallExplosionParticle.FrostmintFactory::new);
        registry.registerSpriteSet(ACParticleRegistry.SUGAR_FLAKE.get(), SugarFlakeParticle.Factory::new);
    }

    public void onItemColors(RegisterColorHandlersEvent.Item event) {
        event.register(
                (stack, colorIn) -> colorIn != 1 ? -1
                        : CaveInfoItem.getBiomeColorOf(Minecraft.getInstance().level, stack, false) | 0xFF000000,
                ACItemRegistry.CAVE_TABLET.get());
        event.register(
                (stack, colorIn) -> colorIn != 1 ? -1
                        : CaveInfoItem.getBiomeColorOf(Minecraft.getInstance().level, stack, false) | 0xFF000000,
                ACItemRegistry.CAVE_CODEX.get());
        event.register((stack, colorIn) -> colorIn != 0 ? -1 : GazingPearlItem.getPearlColor(stack),
                ACItemRegistry.GAZING_PEARL.get());
        event.register((stack, colorIn) -> colorIn != 0 ? -1 : JellyBeanItem.getBeanColor(stack),
                ACItemRegistry.JELLY_BEAN.get());
        event.register(
                (stack, colorIn) -> colorIn != 1 ? -1
                        : BiomeTreatItem.getBiomeTreatColorOf(Minecraft.getInstance().level, stack) | 0xFF000000,
                ACItemRegistry.BIOME_TREAT.get());
    }

    public void onBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (blockState, blockAndTintGetter, blockPos, colorIn) -> colorIn != 0 ? -1
                        : FrostedChocolateBlock.calculateFrostingColor(blockPos),
                ACBlockRegistry.BLOCK_OF_FROSTED_CHOCOLATE.get());
        event.register(
                (blockState, blockAndTintGetter, blockPos, colorIn) -> colorIn != 0 ? -1
                        : FrostedChocolateBlock.calculateFrostingColor(blockPos),
                ACBlockRegistry.BLOCK_OF_FROSTING.get());
    }

    private void onRegisterTooltips(RegisterClientTooltipComponentFactoriesEvent registry) {
        registry.register(SackOfSatingTooltip.class, ClientSackOfSatingTooltip::new);
    }

    private void bakeModels(final ModelEvent.ModifyBakingResult e) {
        if (AlexsCaves.CLIENT_CONFIG.emissiveBlockModels.get()) {
            long time = System.currentTimeMillis();
            for (ModelResourceLocation id : e.getModels().keySet()) {
                if (FULLBRIGHTS.stream().anyMatch(str -> id.toString().startsWith(str))) {
                    e.getModels().put(id, new BakedModelShadeLayerFullbright(e.getModels().get(id)));
                }
            }
            AlexsCaves.LOGGER.info("Loaded emissive block models in {} ms", System.currentTimeMillis() - time);

        }
    }

    private void registerShaders(final RegisterShadersEvent e) {
        try {
            registerInternalShaders((id, vertexFormat, consumer) -> e.registerShader(
                    new ShaderInstance(e.getResourceProvider(), id.toString(), vertexFormat), consumer));
        } catch (IOException exception) {
            AlexsCaves.LOGGER.error("could not register internal shaders");
            exception.printStackTrace();
        }
    }

    private static void registerInternalShaders(ShaderRegistrar registrar) throws IOException {
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_ferrouslime_gel"),
                DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeFerrouslimeGelShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_hologram"),
                DefaultVertexFormat.POSITION_COLOR, ACInternalShaders::setRenderTypeHologramShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_irradiated"),
                DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeIrradiatedShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_blue_irradiated"),
                DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeBlueIrradiatedShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_bubbled"),
                DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeBubbledShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_sepia"),
                DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeSepiaShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_red_ghost"),
                DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypeRedGhostShader);
        registrar.register(ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "rendertype_purple_witch"),
                DefaultVertexFormat.NEW_ENTITY, ACInternalShaders::setRenderTypePurpleWitchShader);
        AlexsCaves.LOGGER.info("registered internal shaders");
    }

    private void registerKeybinds(RegisterKeyMappingsEvent e) {
        e.register(ACKeybindRegistry.KEY_SPECIAL_ABILITY);
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

    public void blockRenderingEntity(UUID id) {
        blockedEntityRenders.add(id);
    }

    public void releaseRenderingEntity(UUID id) {
        blockedEntityRenders.remove(id);
    }

    public void setVisualFlag(int flag) {
    }

    public float getNukeFlashAmount(float partialTicks) {
        return prevNukeFlashAmount + (nukeFlashAmount - prevNukeFlashAmount) * partialTicks;
    }

    public float getPrimordialBossActiveAmount(float partialTicks) {
        return prevPrimordialBossActiveAmount
                + (primordialBossActiveAmount - prevPrimordialBossActiveAmount) * partialTicks;
    }

    public float getPossessionStrengthAmount(float partialTicks) {
        return prevPossessionStrengthAmount + (possessionStrengthAmount - prevPossessionStrengthAmount) * partialTicks;
    }

    public boolean checkIfParticleAt(SimpleParticleType simpleParticleType, BlockPos at) {
        if (!blockedParticleLocations.containsKey(Minecraft.getInstance().level)) {
            blockedParticleLocations.clear();
            blockedParticleLocations.put(Minecraft.getInstance().level, new ArrayList<>());
        }
        List blocked = blockedParticleLocations.get(Minecraft.getInstance().level);
        if (blocked.contains(at)) {
            return false;
        } else {
            blocked.add(new BlockPos(at));
            return true;
        }
    }

    public void removeParticleAt(BlockPos at) {
        if (!blockedParticleLocations.containsKey(Minecraft.getInstance().level)) {
            blockedParticleLocations.clear();
            blockedParticleLocations.put(Minecraft.getInstance().level, new ArrayList<>());
        }
        blockedParticleLocations.get(Minecraft.getInstance().level).remove(at);
    }

    public boolean isKeyDown(int keyType) {
        if (keyType == -1) {
            return Minecraft.getInstance().options.keyLeft.isDown() || Minecraft.getInstance().options.keyRight.isDown()
                    || Minecraft.getInstance().options.keyUp.isDown()
                    || Minecraft.getInstance().options.keyDown.isDown()
                    || Minecraft.getInstance().options.keyJump.isDown();
        }
        if (keyType == 0) {
            return Minecraft.getInstance().options.keyJump.isDown();
        }
        if (keyType == 1) {
            return Minecraft.getInstance().options.keySprint.isDown();
        }
        if (keyType == 2) {
            return ACKeybindRegistry.KEY_SPECIAL_ABILITY.isDown();
        }
        if (keyType == 3) {
            return Minecraft.getInstance().options.keyAttack.isDown();
        }
        if (keyType == 4) {
            return Minecraft.getInstance().options.keyShift.isDown();
        }
        return false;
    }

    @Override
    public Object getISTERProperties() {
        return isterProperties;
    }

    @Override
    public Object getArmorProperties() {
        return armorProperties;
    }

    public float getPartialTicks() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }

    public void setSpelunkeryTutorialComplete(boolean completedTutorial) {
        spelunkeryTutorialComplete = completedTutorial;
    }

    public boolean isSpelunkeryTutorialComplete() {
        return spelunkeryTutorialComplete;
    }

    public void setRenderViewEntity(Player player, Entity entity) {
        if (player == Minecraft.getInstance().player
                && Minecraft.getInstance().getCameraEntity() == Minecraft.getInstance().player) {
            lastPOV = Minecraft.getInstance().options.getCameraType();
            Minecraft.getInstance().setCameraEntity(entity);
            Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);
        }
        if (lastCameraEntity != Minecraft.getInstance().getCameraEntity()) {
            Minecraft.getInstance().levelRenderer.allChanged();
            lastCameraEntity = Minecraft.getInstance().getCameraEntity();
        }
    }

    public void resetRenderViewEntity(Player player) {
        if (player == Minecraft.getInstance().player) {
            Minecraft.getInstance().level = (ClientLevel) Minecraft.getInstance().player.level();
            Minecraft.getInstance().setCameraEntity(Minecraft.getInstance().player);
            Minecraft.getInstance().options.setCameraType(lastPOV);
        }
        if (lastCameraEntity != Minecraft.getInstance().getCameraEntity()) {
            Minecraft.getInstance().levelRenderer.allChanged();
            lastCameraEntity = Minecraft.getInstance().getCameraEntity();
        }
    }

    @Override
    public boolean hasBubbledEffectVisual(int entityId) {
        return BUBBLED_EFFECT_TICKS.getOrDefault(entityId, 0) > 0;
    }

    @Override
    public void setBubbledEffectTicks(int entityId, int ticks) {
        if (ticks <= 0) {
            BUBBLED_EFFECT_TICKS.remove(entityId);
        } else {
            BUBBLED_EFFECT_TICKS.put(entityId, ticks);
        }
    }

    @Override
    public void handleBeholderSync(int beholderId, boolean active, double x, double y, double z, float yRot, float xRot, UUID usingPlayerUUID) {
        Player playerSided = getClientSidePlayer();
        if (playerSided != null && playerSided.level() instanceof ClientLevel clientLevel) {
            Entity watcher = clientLevel.getEntity(beholderId);
            // If entity doesn't exist on client and we have spawn data, create it
            // This is necessary when viewing a Beholder from far away (unloaded chunks)
            if (watcher == null && active && usingPlayerUUID != null) {
                BeholderEyeEntity beholderEye = ACEntityRegistry.BEHOLDER_EYE.get().create(clientLevel);
                if (beholderEye != null) {
                    beholderEye.setId(beholderId);
                    beholderEye.setPos(x, y, z);
                    beholderEye.setEyeYRot(yRot);
                    beholderEye.setEyeXRot(xRot);
                    beholderEye.setUsingPlayerUUID(usingPlayerUUID);
                    beholderEye.hasTakenFullControlOfCamera = true;
                    clientLevel.addEntity(beholderEye);
                    watcher = beholderEye;
                }
            }
            if (watcher instanceof BeholderEyeEntity beholderEye) {
                Entity beholderEyePlayer = beholderEye.getUsingPlayer();
                beholderEye.hasTakenFullControlOfCamera = true;
                if (beholderEyePlayer != null && beholderEyePlayer instanceof Player && beholderEyePlayer.equals(playerSided)) {
                    if (active) {
                        setRenderViewEntity(playerSided, beholderEye);
                    } else {
                        resetRenderViewEntity(playerSided);
                    }
                }
            }
        }
    }

    @Override
    public void playWorldSound(@Nullable Object soundEmitter, byte type) {
        if (soundEmitter instanceof Entity entity && !entity.level().isClientSide) {
            return;
        }
        switch (type) {
            case 0:
                if (soundEmitter instanceof NuclearSirenBlockEntity nuclearSiren) {
                    NuclearSirenSound sound;
                    AbstractTickableSoundInstance old = BLOCK_ENTITY_SOUND_INSTANCE_MAP.get(nuclearSiren);
                    if (old == null || !(old instanceof NuclearSirenSound nuclearSirenSound
                            && nuclearSirenSound.isSameBlockEntity(nuclearSiren)) || old.isStopped()) {
                        sound = new NuclearSirenSound(nuclearSiren);
                        BLOCK_ENTITY_SOUND_INSTANCE_MAP.put(nuclearSiren, sound);
                    } else {
                        sound = (NuclearSirenSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 1:
                if (soundEmitter instanceof NucleeperEntity nucleeper) {
                    NucleeperSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(nucleeper.getId());
                    if (old == null || !(old instanceof NucleeperSound nucleeperSound
                            && nucleeperSound.isSameEntity(nucleeper))) {
                        sound = new NucleeperSound(nucleeper);
                        ENTITY_SOUND_INSTANCE_MAP.put(nucleeper.getId(), sound);
                    } else {
                        sound = (NucleeperSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 2:
                if (soundEmitter instanceof NotorEntity notor) {
                    NotorHologramSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(notor.getId());
                    if (old == null || !(old instanceof NotorHologramSound hologramSound
                            && hologramSound.isSameEntity(notor))) {
                        sound = new NotorHologramSound(notor);
                        ENTITY_SOUND_INSTANCE_MAP.put(notor.getId(), sound);
                    } else {
                        sound = (NotorHologramSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 3:
                if (soundEmitter instanceof HologramProjectorBlockEntity hologramProjector) {
                    HologramProjectorSound sound;
                    AbstractTickableSoundInstance old = BLOCK_ENTITY_SOUND_INSTANCE_MAP.get(hologramProjector);
                    if (old == null || !(old instanceof HologramProjectorSound hologramSound
                            && hologramSound.isSameBlockEntity(hologramProjector)) || old.isStopped()) {
                        sound = new HologramProjectorSound(hologramProjector);
                        BLOCK_ENTITY_SOUND_INSTANCE_MAP.put(hologramProjector, sound);
                    } else {
                        sound = (HologramProjectorSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 4:
                if (soundEmitter instanceof MagnetBlockEntity magnet) {
                    MagnetSound sound;
                    AbstractTickableSoundInstance old = BLOCK_ENTITY_SOUND_INSTANCE_MAP.get(magnet);
                    if (old == null
                            || !(old instanceof MagnetSound magnetSound && magnetSound.isSameBlockEntity(magnet))
                            || old.isStopped()) {
                        sound = new MagnetSound(magnet);
                        BLOCK_ENTITY_SOUND_INSTANCE_MAP.put(magnet, sound);
                    } else {
                        sound = (MagnetSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 5:
                if (soundEmitter instanceof UnderzealotEntity underzealot) {
                    UnderzealotSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(underzealot.getId());
                    if (old == null || !(old instanceof UnderzealotSound underzealotSound
                            && underzealotSound.isSameEntity(underzealot))) {
                        sound = new UnderzealotSound(underzealot);
                        ENTITY_SOUND_INSTANCE_MAP.put(underzealot.getId(), sound);
                    } else {
                        sound = (UnderzealotSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 6:
                if (soundEmitter instanceof CorrodentEntity corrodent) {
                    CorrodentSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(corrodent.getId());
                    if (old == null || !(old instanceof CorrodentSound corrodentSound
                            && corrodentSound.isSameEntity(corrodent))) {
                        sound = new CorrodentSound(corrodent);
                        ENTITY_SOUND_INSTANCE_MAP.put(corrodent.getId(), sound);
                    } else {
                        sound = (CorrodentSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 7:
                if (soundEmitter instanceof NuclearFurnaceBlockEntity nuclearFurnace) {
                    NuclearFurnaceSound sound;
                    AbstractTickableSoundInstance old = BLOCK_ENTITY_SOUND_INSTANCE_MAP.get(nuclearFurnace);
                    if (old == null || !(old instanceof NuclearFurnaceSound furnaceSound
                            && furnaceSound.isSameBlockEntity(nuclearFurnace)) || old.isStopped()) {
                        sound = new NuclearFurnaceSound(nuclearFurnace);
                        BLOCK_ENTITY_SOUND_INSTANCE_MAP.put(nuclearFurnace, sound);
                    } else {
                        sound = (NuclearFurnaceSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 8:
                if (soundEmitter instanceof LivingEntity livingEntity) {
                    RaygunSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(livingEntity.getId());
                    if (old == null
                            || !(old instanceof RaygunSound raygunSound && raygunSound.isSameEntity(livingEntity))) {
                        sound = new RaygunSound(livingEntity);
                        ENTITY_SOUND_INSTANCE_MAP.put(livingEntity.getId(), sound);
                    } else {
                        sound = (RaygunSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 9:
                if (soundEmitter instanceof LivingEntity livingEntity) {
                    ResistorShieldSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(livingEntity.getId());
                    if (old == null || !(old instanceof ResistorShieldSound resistorShieldSound
                            && resistorShieldSound.isSameEntity(livingEntity) && !resistorShieldSound.isAzure())) {
                        sound = new ResistorShieldSound(livingEntity, false);
                        ENTITY_SOUND_INSTANCE_MAP.put(livingEntity.getId(), sound);
                    } else {
                        sound = (ResistorShieldSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 10:
                if (soundEmitter instanceof LivingEntity livingEntity) {
                    ResistorShieldSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(livingEntity.getId());
                    if (old == null || !(old instanceof ResistorShieldSound resistorShieldSound
                            && resistorShieldSound.isSameEntity(livingEntity) && resistorShieldSound.isAzure())) {
                        sound = new ResistorShieldSound(livingEntity, true);
                        ENTITY_SOUND_INSTANCE_MAP.put(livingEntity.getId(), sound);
                    } else {
                        sound = (ResistorShieldSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 11:
                if (soundEmitter instanceof LivingEntity livingEntity) {
                    GalenaGauntletSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(livingEntity.getId());
                    if (old == null || !(old instanceof GalenaGauntletSound gauntletSound
                            && gauntletSound.isSameEntity(livingEntity))) {
                        sound = new GalenaGauntletSound(livingEntity);
                        ENTITY_SOUND_INSTANCE_MAP.put(livingEntity.getId(), sound);
                    } else {
                        sound = (GalenaGauntletSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 12:
                if (soundEmitter instanceof BoundroidEntity boundroid) {
                    BoundroidSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(boundroid.getId());
                    if (old == null || !(old instanceof BoundroidSound boundroidSound
                            && boundroidSound.isSameEntity(boundroid))) {
                        sound = new BoundroidSound(boundroid);
                        ENTITY_SOUND_INSTANCE_MAP.put(boundroid.getId(), sound);
                    } else {
                        sound = (BoundroidSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 13:
                if (soundEmitter instanceof FerrouslimeEntity ferrouslime) {
                    FerrouslimeSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(ferrouslime.getId());
                    if (old == null || !(old instanceof FerrouslimeSound ferrouslimeSound
                            && ferrouslimeSound.isSameEntity(ferrouslime))) {
                        sound = new FerrouslimeSound(ferrouslime);
                        ENTITY_SOUND_INSTANCE_MAP.put(ferrouslime.getId(), sound);
                    } else {
                        sound = (FerrouslimeSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 14:
                if (soundEmitter instanceof QuarrySmasherEntity quarrySmasher) {
                    QuarrySmasherSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(quarrySmasher.getId());
                    if (old == null || !(old instanceof QuarrySmasherSound quarrySmasherSound
                            && quarrySmasherSound.isSameEntity(quarrySmasher))) {
                        sound = new QuarrySmasherSound(quarrySmasher);
                        ENTITY_SOUND_INSTANCE_MAP.put(quarrySmasher.getId(), sound);
                    } else {
                        sound = (QuarrySmasherSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 15:
                if (soundEmitter instanceof SubmarineEntity submarine) {
                    SubmarineSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(submarine.getId());
                    if (old == null || !(old instanceof SubmarineSound submarineSound
                            && submarineSound.isSameEntity(submarine))) {
                        sound = new SubmarineSound(submarine);
                        ENTITY_SOUND_INSTANCE_MAP.put(submarine.getId(), sound);
                    } else {
                        sound = (SubmarineSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 16:
                if (soundEmitter instanceof TremorzillaEntity tremorzilla) {
                    TremorzillaBeamSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(tremorzilla.getId());
                    if (old == null || !(old instanceof TremorzillaBeamSound tremorzillaBeamSound
                            && tremorzillaBeamSound.isSameEntity(tremorzilla))) {
                        sound = new TremorzillaBeamSound(tremorzilla);
                        ENTITY_SOUND_INSTANCE_MAP.put(tremorzilla.getId(), sound);
                    } else {
                        sound = (TremorzillaBeamSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 17:
                if (soundEmitter instanceof GumWormEntity gumWorm) {
                    GumWormSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(gumWorm.getId());
                    if (old == null
                            || !(old instanceof GumWormSound gumWormSound && gumWormSound.isSameEntity(gumWorm))) {
                        sound = new GumWormSound(gumWorm);
                        ENTITY_SOUND_INSTANCE_MAP.put(gumWorm.getId(), sound);
                    } else {
                        sound = (GumWormSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 18:
                if (soundEmitter instanceof LivingEntity livingEntity) {
                    SugarRushSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(livingEntity.getId());
                    if (old == null || !(old instanceof SugarRushSound sugarRushSound
                            && sugarRushSound.isSameEntity(livingEntity))) {
                        sound = new SugarRushSound(livingEntity);
                        ENTITY_SOUND_INSTANCE_MAP.put(livingEntity.getId(), sound);
                    } else {
                        sound = (SugarRushSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
            case 19:
                if (soundEmitter instanceof CandicornEntity candicorn) {
                    CandicornSound sound;
                    AbstractTickableSoundInstance old = ENTITY_SOUND_INSTANCE_MAP.get(candicorn.getId());
                    if (old == null || !(old instanceof CandicornSound candicornSound
                            && candicornSound.isSameEntity(candicorn))) {
                        sound = new CandicornSound(candicorn);
                        ENTITY_SOUND_INSTANCE_MAP.put(candicorn.getId(), sound);
                    } else {
                        sound = (CandicornSound) old;
                    }
                    if (!isSoundPlaying(sound) && sound.canPlaySound()) {
                        Minecraft.getInstance().getSoundManager().queueTickingSound(sound);
                    }
                }
                break;
        }
    }

    private boolean isSoundPlaying(AbstractTickableSoundInstance sound) {
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        SoundEngine soundEngine = ((SoundManagerAccessor) soundManager).getSoundEngine();
        SoundEngineAccessor engineAccessor = (SoundEngineAccessor) soundEngine;
        // In 1.21, tickingSounds is a List, not a Map
        return engineAccessor.getQueuedTickableSounds().contains(sound)
                || engineAccessor.getTickingSounds().contains(sound);
    }

    public void playWorldEvent(int messageId, Level level, BlockPos pos) {
        if (messageId == 0 && AcidBlock.doesBlockCorrode(level.getBlockState(pos))) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
                    ACSoundRegistry.ACID_CORROSION.get(), SoundSource.BLOCKS, 0.5F,
                    level.random.nextFloat() * 0.4F + 0.8F, false);
        }
        if (messageId == 1 && level.getBlockState(pos).getBlock() instanceof ActivatedByAltar) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
                    ACSoundRegistry.ABYSSMARINE_GLOW_ON.get(), SoundSource.BLOCKS, 1.5F,
                    level.random.nextFloat() * 0.4F + 0.8F, false);
        }
        if (messageId == 2 && level.getBlockState(pos).getBlock() instanceof ActivatedByAltar) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
                    ACSoundRegistry.ABYSSMARINE_GLOW_OFF.get(), SoundSource.BLOCKS, 1.5F,
                    level.random.nextFloat() * 0.4F + 0.8F, false);
        }
        if (messageId == 3 && level.getBlockState(pos).is(ACBlockRegistry.DRAIN.get())) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
                    ACSoundRegistry.DRAIN_START.get(), SoundSource.BLOCKS, 1.5F, level.random.nextFloat() * 0.4F + 0.8F,
                    false);
        }
        if (messageId == 4 && level.getBlockState(pos).is(ACBlockRegistry.DRAIN.get())) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
                    ACSoundRegistry.DRAIN_STOP.get(), SoundSource.BLOCKS, 1.5F, level.random.nextFloat() * 0.4F + 0.8F,
                    false);
        }
        if (messageId == 5 && level.getBlockState(pos).is(ACBlockRegistry.SPELUNKERY_TABLE.get())) {
            level.playLocalSound((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D,
                    ACSoundRegistry.SPELUNKERY_TABLE_FAIL.get(), SoundSource.BLOCKS, 1.5F,
                    level.random.nextFloat() * 0.4F + 0.8F, false);
            BlockParticleOption blockparticleoption = new BlockParticleOption(ParticleTypes.BLOCK,
                    Blocks.STONE.defaultBlockState());
            for (int i = 0; i < 8; i++) {
                level.addParticle(blockparticleoption, pos.getX() + level.random.nextFloat(), pos.getY() + 1.0F,
                        pos.getZ() + level.random.nextFloat(), 0, 0, 0);
            }
        }
        if (messageId == 6 && level.getBlockState(pos).is(ACBlockRegistry.ABYSSAL_ALTAR.get())
                && level.getBlockEntity(pos) instanceof AbyssalAltarBlockEntity altarBlock) {
            altarBlock.resetSlideAnimation();
        }
        if (messageId == 7) {
            for (int i = 0; i < 8; i++) {
                level.addParticle(ACParticleRegistry.PURPLE_WITCH_EXPLOSION.get(),
                        pos.getX() + level.random.nextFloat(), pos.getY() + level.random.nextFloat(),
                        pos.getZ() + level.random.nextFloat(), 0, 0, 0);
            }
        }
        if (messageId == 8) {
            for (int i = 0; i < 15; i++) {
                float particleX = random.nextFloat() * 8 - 4;
                float particleY = random.nextFloat() * 8 - 4;
                float particleZ = random.nextFloat() * 8 - 4;
                level.addAlwaysVisibleParticle(
                        random.nextInt(5) == 0 ? ACParticleRegistry.FROSTMINT_EXPLOSION.get() : ParticleTypes.SNOWFLAKE,
                        true, pos.getX() + particleX, pos.getY() + particleY, pos.getZ() + particleZ, 0, 0, 0);
            }
        }
        if (messageId == 9) {
            for (int i = 0; i < 30; i++) {
                float particleX = random.nextFloat() * 4 - 2;
                float particleY = random.nextFloat() * 4 - 2;
                float particleZ = random.nextFloat() * 4 - 2;
                level.addAlwaysVisibleParticle(ParticleTypes.SNOWFLAKE, true, pos.getX() + particleX,
                        pos.getY() + particleY, pos.getZ() + particleZ, 0, 0, 0);
            }
        }
    }

    public void clearSoundCacheFor(Entity entity) {
        ENTITY_SOUND_INSTANCE_MAP.remove(entity.getId());
    }

    public void clearSoundCacheFor(BlockEntity entity) {
        BLOCK_ENTITY_SOUND_INSTANCE_MAP.remove(entity);
    }

    public Vec3 getDarknessTrailPosFor(LivingEntity living, int pointer, float partialTick) {
        if (living.isRemoved()) {
            partialTick = 1.0F;
        }
        Vec3[] trailPositions = darknessTrailPosMap.get(living);
        if (trailPositions == null || !darknessTrailPointerMap.containsKey(living)) {
            return living.position();
        }
        int trailPointer = darknessTrailPointerMap.get(living);
        int i = trailPointer - pointer & 63;
        int j = trailPointer - pointer - 1 & 63;
        Vec3 d0 = trailPositions[j];
        Vec3 d1 = trailPositions[i].subtract(d0);
        return d0.add(d1.scale(partialTick));
    }

    public int getPlayerTime() {
        return Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.tickCount;
    }

    public void preScreenRender(float partialTick) {
        float screenEffectIntensity = Minecraft.getInstance().options.screenEffectScale().get().floatValue();
        float watcherPossessionStrength = getPossessionStrengthAmount(partialTick);
        float nukeFlashAmount = getNukeFlashAmount(partialTick);
        if (nukeFlashAmount > 0 && (AlexsCaves.CLIENT_CONFIG.nuclearBombFlash.get())) {
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, nukeFlashAmount * screenEffectIntensity);
            RenderSystem.setShaderTexture(0, ClientProxy.BOMB_FLASH);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.addVertex(0.0F, (float) screenHeight, -90.0F).setUv(0.0F, 1.0F);
            bufferbuilder.addVertex((float) screenWidth, (float) screenHeight, -90.0F).setUv(1.0F, 1.0F);
            bufferbuilder.addVertex((float) screenWidth, 0.0F, -90.0F).setUv(1.0F, 0.0F);
            bufferbuilder.addVertex(0.0F, 0.0F, -90.0F).setUv(0.0F, 0.0F);
            BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
        if (watcherPossessionStrength > 0) {
            int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
            int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, watcherPossessionStrength * screenEffectIntensity);
            RenderSystem.setShaderTexture(0, ClientProxy.WATCHER_EFFECT);
            Tesselator tesselator2 = Tesselator.getInstance();
            BufferBuilder bufferbuilder2 = tesselator2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder2.addVertex(0.0F, (float) screenHeight, -90.0F).setUv(0.0F, 1.0F);
            bufferbuilder2.addVertex((float) screenWidth, (float) screenHeight, -90.0F).setUv(1.0F, 1.0F);
            bufferbuilder2.addVertex((float) screenWidth, 0.0F, -90.0F).setUv(1.0F, 0.0F);
            bufferbuilder2.addVertex(0.0F, 0.0F, -90.0F).setUv(0.0F, 0.0F);
            BufferUploader.drawWithShader(bufferbuilder2.buildOrThrow());
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean isFirstPersonPlayer(Entity entity) {
        return entity.equals(Minecraft.getInstance().cameraEntity)
                && Minecraft.getInstance().options.getCameraType().isFirstPerson();
    }

    public void openBookGUI(ItemStack itemStackIn) {
        Minecraft.getInstance().setScreen(new CaveBookScreen());
    }

    @Override
    public Vec3 getCameraRotation() {
        return Vec3.ZERO;
    }

    public void setPrimordialBossActive(Level level, int id, boolean active) {
        if (level.isClientSide) {
            primordialBossActive = active;
            if (!active && id != -1) {
                Minecraft.getInstance().getMusicManager().stopPlaying(ACMusics.LUXTRUCTOSAURUS_BOSS_MUSIC);
            }
        } else {
            super.setPrimordialBossActive(level, id, active);
        }
    }

    public boolean isPrimordialBossActive(Level level) {
        return level.isClientSide ? primordialBossActive : super.isPrimordialBossActive(level);
    }

    public static Vec3 processSkyColor(Vec3 colorIn, float partialTick) {
        float primordialAmount = AlexsCaves.PROXY.getPrimordialBossActiveAmount(partialTick);
        if (primordialAmount > 0.0F) {
            Vec3 targetColor = new Vec3(0.2F, 0.15F, 0.1F);
            colorIn = colorIn.add(targetColor.subtract(colorIn).scale(primordialAmount));
        }
        return colorIn;
    }

    public void removeBossBarRender(UUID bossBar) {
        bossBarRenderTypes.remove(bossBar);
    }

    public void setBossBarRender(UUID bossBar, int renderType) {
        bossBarRenderTypes.put(bossBar, renderType);
    }

    public boolean isTickRateModificationActive(Level level) {
        return ClientTickRateTracker.getForClient(Minecraft.getInstance()).getClientTickRate() != 50;
    }

    @Override
    public boolean isFarFromCamera(double x, double y, double z) {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceToSqr(x, y, z) >= 256.0D;
    }

    public void renderVanillaMapDecoration(MapDecoration mapDecoration, int index) {
        ClientEvents.renderVanillaMapDecoration(mapDecoration, index + 1);
    }
}
