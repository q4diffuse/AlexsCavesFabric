package com.github.alexmodguy.alexscaves.client.event;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.citadel.client.tick.ClientTickRateTracker;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.mixin.client.CameraAccessor;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.SubmarineEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import com.github.alexmodguy.alexscaves.server.entity.util.PossessesCamera;
import com.github.alexmodguy.alexscaves.server.entity.util.RidingMeterMount;
import com.github.alexmodguy.alexscaves.server.item.*;
import com.github.alexmodguy.alexscaves.server.misc.ACVanillaMapUtil;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.potion.DarknessIncarnateEffect;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.UUID;

import static net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY;

public class ClientEvents {

    public static PoseStack lastVanillaMapPoseStack;
    public static MultiBufferSource lastVanillaMapRenderBuffer;
    public static int lastVanillaMapRenderPackedLight;
    private static final RenderType UNDERGROUND_CABIN_MAP_ICONS = RenderType.text(
        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/underground_cabin_map_icons.png")
    );
    private static final ResourceLocation POTION_EFFECT_HUD_OVERLAYS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/potion_effect_hud_overlays.png");
    private static final ResourceLocation BOSS_BAR_HUD_OVERLAYS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/boss_bar_hud_overlays.png");
    private static final ResourceLocation DINOSAUR_HUD_OVERLAYS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/dinosaur_hud_overlays.png");
    private static final ResourceLocation ARMOR_HUD_OVERLAYS = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/misc/armor_hud_overlays.png");
    private static final ResourceLocation TRAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "textures/particle/teletor_trail.png");
    // NeoForge's Gui.leftHeight/rightHeight are reset to 39 before the health/food layers draw
    private static final int VANILLA_GUI_HEIGHT = 39;

    public static void renderVanillaMapDecoration(MapDecoration mapDecoration, int index) {
        if (!ACVanillaMapUtil.isUndergroundCabinDecoration(mapDecoration.type())) {
            return;
        }
        MultiBufferSource multiBufferSource = lastVanillaMapRenderBuffer == null
            ? Minecraft.getInstance().renderBuffers().bufferSource()
            : lastVanillaMapRenderBuffer;
        PoseStack poseStack = lastVanillaMapPoseStack == null ? new PoseStack() : lastVanillaMapPoseStack;
        poseStack.pushPose();
        poseStack.translate((float) mapDecoration.x() / 2.0F + 64.0F, (float) mapDecoration.y() / 2.0F + 64.0F, -0.02F);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (mapDecoration.rot() * 360) / 16.0F));
        poseStack.scale(4.0F, 4.0F, 3.0F);
        poseStack.translate(-0.125F, 0.125F, 0.0F);
        byte icon = ACVanillaMapUtil.getMapIconRenderOrdinal(mapDecoration.type());
        float u0 = (float) (icon % 16) / 16.0F;
        float v0 = (float) (icon / 16) / 16.0F;
        float u1 = (float) (icon % 16 + 1) / 16.0F;
        float v1 = (float) (icon / 16 + 1) / 16.0F;
        Matrix4f pose = poseStack.last().pose();
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(UNDERGROUND_CABIN_MAP_ICONS);
        vertexConsumer.addVertex(pose, -1.0F, 1.0F, (float) index * -0.001F).setColor(255, 255, 255, 255).setUv(u0, v0).setLight(lastVanillaMapRenderPackedLight);
        vertexConsumer.addVertex(pose, 1.0F, 1.0F, (float) index * -0.001F).setColor(255, 255, 255, 255).setUv(u1, v0).setLight(lastVanillaMapRenderPackedLight);
        vertexConsumer.addVertex(pose, 1.0F, -1.0F, (float) index * -0.001F).setColor(255, 255, 255, 255).setUv(u1, v1).setLight(lastVanillaMapRenderPackedLight);
        vertexConsumer.addVertex(pose, -1.0F, -1.0F, (float) index * -0.001F).setColor(255, 255, 255, 255).setUv(u0, v1).setLight(lastVanillaMapRenderPackedLight);
        poseStack.popPose();

        mapDecoration.name().ifPresent(component -> {
            Font font = Minecraft.getInstance().font;
            float width = font.width(component);
            float scale = Mth.clamp(25.0F / width, 0.0F, 6.0F / 9.0F);
            poseStack.pushPose();
            poseStack.translate((float) mapDecoration.x() / 2.0F + 64.0F - width * scale / 2.0F, (float) mapDecoration.y() / 2.0F + 68.0F, -0.025F);
            poseStack.scale(scale, scale, 1.0F);
            poseStack.translate(0.0F, 0.0F, -0.1F);
            font.drawInBatch(component, 0.0F, 0.0F, -1, false, poseStack.last().pose(), multiBufferSource, Font.DisplayMode.NORMAL, Integer.MIN_VALUE, lastVanillaMapRenderPackedLight);
            poseStack.popPose();
        });
    }

    /**
     * Extra third person camera distance for large mounts.
     * Called at the end of Camera.setup, where NeoForge fired ViewportEvent.ComputeCameraAngles.
     */
    public static void computeCameraAngles(Camera camera, Entity player, float partialTick) {
        if (player == null) {
            return;
        }
        if (player.isPassenger() && camera.isDetached()) {
            float extraZoom = 0.0F;
            Entity vehicle = player.getVehicle();
            if (vehicle instanceof SubmarineEntity || vehicle instanceof AtlatitanEntity) {
                extraZoom = 4.0F;
            } else if (vehicle instanceof TremorsaurusEntity) {
                extraZoom = 2.0F;
            } else if (vehicle instanceof TremorzillaEntity) {
                extraZoom = 10.0F;
            } else if (vehicle instanceof GumWormSegmentEntity) {
                extraZoom = 12.0F;
            }
            if (extraZoom > 0.0F) {
                CameraAccessor cameraAccessor = (CameraAccessor) camera;
                cameraAccessor.invokeMove(-cameraAccessor.invokeGetMaxZoom(extraZoom), 0, 0);
            }
        }
    }

    /**
     * Camera roll in degrees, applied to the view matrix in GameRenderer.renderLevel like NeoForge's ComputeCameraAngles roll.
     */
    public static float getCameraRoll(float partialTick) {
        Entity player = Minecraft.getInstance().getCameraEntity();
        if (player instanceof LivingEntity livingEntity && livingEntity.hasEffect(ACEffectRegistry.STUNNED)) {
            return (float) (Math.sin((player.tickCount + partialTick) * 0.2F) * 10F);
        }
        return 0.0F;
    }

    /**
     * NeoForge renders the local player when the camera is another entity; vanilla does not.
     * Needed to see yourself through a possessed Watcher.
     */
    public static boolean shouldRenderLocalPlayerFromCamera() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.getCameraEntity() instanceof PossessesCamera && minecraft.player != null && !minecraft.player.isSpectator();
    }

    public static double computeFov(Camera camera, double fov) {
        if (camera.getEntity() instanceof PossessesCamera) {
            fov = 90;
        }
        Player player = Minecraft.getInstance().player;
        if (player != null && player.isPassenger() && player.getVehicle() instanceof SubmarineEntity && camera.getFluidInCamera() == FogType.WATER) {
            float f = (float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0D, 0.85714287F);
            fov = fov / f;
        }
        return fov;
    }

    public static float computeFovModifier(Player player, float fovModifier) {
        if (player.isUsingItem() && player.getUseItem().is(ACItemRegistry.DREADBOW.get())) {
            float f1 = (float) player.getTicksUsingItem() / 20.0F;
            if (f1 > 1.0F) {
                f1 = 1.0F;
            } else {
                f1 *= f1;
            }
            fovModifier *= 1.0F - f1 * 0.15F;
        }
        return fovModifier;
    }

    public static boolean isCameraPossessed() {
        return Minecraft.getInstance().getCameraEntity() instanceof PossessesCamera;
    }

    public static boolean shouldHideExperienceBar() {
        Entity player = Minecraft.getInstance().getCameraEntity();
        return player instanceof PossessesCamera || player != null && player.getVehicle() instanceof RidingMeterMount dinosaur && dinosaur.hasRidingMeter();
    }

    /**
     * @return the outline color override for this entity, or null to keep the vanilla team color
     */
    public static Integer getOutlineColor(Entity entity) {
        Player player = Minecraft.getInstance().player;
        Integer color = null;
        if (player != null && player.getUseItem().is(ACItemRegistry.TOTEM_OF_POSSESSION.get())) {
            UUID boundUUID = TotemOfPossessionItem.getBoundEntityUUID(player.getUseItem());
            if (boundUUID != null && boundUUID.equals(entity.getUUID())) {
                color = 0xFF0000;
            }
        }
        if (entity instanceof ItemEntity item) {
            if (item.getItem().is(ACItemRegistry.TECTONIC_SHARD.get())) {
                color = 0XFFDB00;
            }
            if (item.getItem().is(ACItemRegistry.SWEET_TOOTH.get())) {
                color = 0XFF8ACD;
            }
        }
        return color;
    }

    public static void tickDarknessTrails(Iterable<Entity> entities) {
        ClientProxy.darknessTrailPosMap.keySet().removeIf(Entity::isRemoved);
        ClientProxy.darknessTrailPointerMap.keySet().removeIf(Entity::isRemoved);
        for (Entity e : entities) {
            if (!(e instanceof LivingEntity entity)) {
                continue;
            }
            if (entity.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE) && entity.isAlive()) {
                int trailPointer = ClientProxy.darknessTrailPointerMap.getOrDefault(entity, -1);
                Vec3 latest = entity.position();
                if (ClientProxy.darknessTrailPosMap.get(entity) == null) {
                    Vec3[] trailPositions = new Vec3[64];
                    if (trailPointer == -1) {
                        Arrays.fill(trailPositions, latest);
                    }
                    ClientProxy.darknessTrailPosMap.put(entity, trailPositions);
                }
                if (++trailPointer == ClientProxy.darknessTrailPosMap.get(entity).length) {
                    trailPointer = 0;
                }
                ClientProxy.darknessTrailPointerMap.put(entity, trailPointer);
                ClientProxy.darknessTrailPosMap.get(entity)[trailPointer] = latest;
            } else if (ClientProxy.darknessTrailPosMap.containsKey(entity)) {
                ClientProxy.darknessTrailPosMap.remove(entity);
                ClientProxy.darknessTrailPointerMap.remove(entity);
            }
        }
    }

    public static void renderDarknessTrail(LivingEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn) {
        if (!entity.hasEffect(ACEffectRegistry.DARKNESS_INCARNATE) || !entity.isAlive()) {
            return;
        }
        Vec3 trailOffset = new Vec3(0, entity.getBbHeight() * 0.5F, 0);
        double x = Mth.lerp(partialTick, entity.xOld, entity.getX());
        double y = Mth.lerp(partialTick, entity.yOld, entity.getY());
        double z = Mth.lerp(partialTick, entity.zOld, entity.getZ());
        int samples = 0;
        int sampleSize = 60;
        float trailHeight = entity.getBbHeight() * 0.8F;
        Vec3 topAngleVec = new Vec3(0, trailHeight, 0);
        Vec3 bottomAngleVec = new Vec3(0, -trailHeight, 0);
        Vec3 drawFrom = trailOffset;
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE));
        float trailA = DarknessIncarnateEffect.getIntensity(entity, partialTick, 20F);
        while (samples < sampleSize) {
            Vec3 sample = AlexsCaves.PROXY.getDarknessTrailPosFor(entity, samples + 5, partialTick).subtract(x, y, z).add(trailOffset);
            float u1 = samples / (float) sampleSize;
            float u2 = u1 + 1 / (float) sampleSize;
            Vec3 draw1 = drawFrom;
            Vec3 draw2 = sample;
            Matrix4f matrix4f = poseStack.last().pose();
            vertexconsumer.addVertex(matrix4f, (float) draw1.x + (float) bottomAngleVec.x, (float) draw1.y + (float) bottomAngleVec.y, (float) draw1.z + (float) bottomAngleVec.z).setColor(0, 0, 0, trailA).setUv(u1, 1F).setOverlay(NO_OVERLAY).setLight(packedLightIn).setNormal(0.0F, 1.0F, 0.0F);
            vertexconsumer.addVertex(matrix4f, (float) draw2.x + (float) bottomAngleVec.x, (float) draw2.y + (float) bottomAngleVec.y, (float) draw2.z + (float) bottomAngleVec.z).setColor(0, 0, 0, trailA).setUv(u2, 1F).setOverlay(NO_OVERLAY).setLight(packedLightIn).setNormal(0.0F, 1.0F, 0.0F);
            vertexconsumer.addVertex(matrix4f, (float) draw2.x + (float) topAngleVec.x, (float) draw2.y + (float) topAngleVec.y, (float) draw2.z + (float) topAngleVec.z).setColor(0, 0, 0, trailA).setUv(u2, 0).setOverlay(NO_OVERLAY).setLight(packedLightIn).setNormal(0.0F, 1.0F, 0.0F);
            vertexconsumer.addVertex(matrix4f, (float) draw1.x + (float) topAngleVec.x, (float) draw1.y + (float) topAngleVec.y, (float) draw1.z + (float) topAngleVec.z).setColor(0, 0, 0, trailA).setUv(u1, 0).setOverlay(NO_OVERLAY).setLight(packedLightIn).setNormal(0.0F, 1.0F, 0.0F);
            samples++;
            drawFrom = sample;
        }
    }

    /**
     * Draws an Alex's Caves styled boss bar in place of the vanilla one.
     * @return the vertical increment for this bar, or -1 to let vanilla draw it
     */
    public static int renderBossBar(GuiGraphics guiGraphics, int x, int y, LerpingBossEvent bossEvent) {
        Integer renderTypeFor = ClientProxy.bossBarRenderTypes.get(bossEvent.getId());
        if (renderTypeFor == null || renderTypeFor != 0) {
            return -1;
        }
        int i = guiGraphics.guiWidth();
        Component component = bossEvent.getName();
        guiGraphics.blit(BOSS_BAR_HUD_OVERLAYS, x, y, 0, 0, 182, 15);
        int progressScaled = (int) (bossEvent.getProgress() * 183.0F);
        guiGraphics.blit(BOSS_BAR_HUD_OVERLAYS, x, y, 0, 15, progressScaled, 15);
        int l = Minecraft.getInstance().font.width(component);
        int i1 = i / 2 - l / 2;
        int j1 = y - 9;
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(i1, j1, 0);
        Minecraft.getInstance().font.drawInBatch8xOutline(component.getVisualOrderText(), 0.0F, 0.0F, 0XFF5100, 0X361515, poseStack.last().pose(), guiGraphics.bufferSource(), 240);
        poseStack.popPose();
        return 10 + 9 + 7;
    }

    public static void renderHotbarOverlays(GuiGraphics guiGraphics) {
        Player player = AlexsCaves.PROXY.getClientSidePlayer();
        if (player == null) {
            return;
        }
        int hudY = 0;
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        if (player.getVehicle() instanceof RidingMeterMount mount && mount.hasRidingMeter()) {
            int forgeGuiY = VANILLA_GUI_HEIGHT;
            if (player.getArmorValue() > 0 && mount instanceof SubterranodonEntity) {
                forgeGuiY += 25;
            }
            if (forgeGuiY < 53) {
                forgeGuiY = 53;
            }
            int j = screenWidth / 2 - AlexsCaves.CLIENT_CONFIG.subterranodonIndicatorX.get();
            int k = screenHeight - forgeGuiY - AlexsCaves.CLIENT_CONFIG.subterranodonIndicatorY.get();
            float f = mount.getMeterAmount();
            float invProgress = 1 - f;
            int uOffset = 0;
            int vOffset = 0;
            int dinoHeight = 31;
            if (mount instanceof TremorsaurusEntity) {
                vOffset = 63;
                k += 5;
                hudY = 20;
            } else if (mount instanceof AtlatitanEntity) {
                vOffset = 126;
                dinoHeight = 32;
                k += 3;
                hudY = 40;
            } else if (mount instanceof TremorzillaEntity tremorzilla) {
                vOffset = 193;
                if (tremorzilla.isPowered() && !tremorzilla.isFiring() && tremorzilla.getSpikesDownAmount() > 0) {
                    if (tremorzilla.tickCount / 2 % 2 == 1) {
                        vOffset = 251;
                    }
                    invProgress = 1F;
                }
                dinoHeight = 29;
                k += 5;
                hudY = 20;
            } else if (mount instanceof CandicornEntity) {
                vOffset = 280;
                dinoHeight = 25;
                hudY = 40;
                k += 4;
            } else {
                hudY = 40;
            }
            guiGraphics.pose().pushPose();
            guiGraphics.blit(DINOSAUR_HUD_OVERLAYS, j, k, 50, uOffset, vOffset + dinoHeight, 43, dinoHeight, 128, 512);
            guiGraphics.blit(DINOSAUR_HUD_OVERLAYS, j, k, 50, uOffset, vOffset, 43, (int) Math.floor(dinoHeight * invProgress), 128, 512);
            guiGraphics.pose().popPose();
        }
        if (DarknessArmorItem.hasMeter(player)) {
            ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
            int forgeGuiY = Math.max(VANILLA_GUI_HEIGHT, 53);
            int j = screenWidth / 2 - AlexsCaves.CLIENT_CONFIG.subterranodonIndicatorX.get() + 13;
            int k = screenHeight - forgeGuiY - AlexsCaves.CLIENT_CONFIG.subterranodonIndicatorY.get() + 9 - hudY;
            float f = DarknessArmorItem.getMeterProgress(stack);
            float invProgress = 1 - f;
            int uvOffset = DarknessArmorItem.canChargeUp(stack) && f >= 1.0F ? 0 : 18;
            guiGraphics.pose().pushPose();
            guiGraphics.blit(ARMOR_HUD_OVERLAYS, j, k, 50, uvOffset, 19, 18, 19, 128, 128);
            guiGraphics.blit(ARMOR_HUD_OVERLAYS, j, k, 50, 0, 0, 18, (int) Math.floor(19 * invProgress), 128, 128);
            guiGraphics.pose().popPose();
        }
    }

    public static void renderIrradiatedHearts(GuiGraphics guiGraphics) {
        Player player = AlexsCaves.PROXY.getClientSidePlayer();
        if (player == null || !(Minecraft.getInstance().getCameraEntity() instanceof Player) || !player.hasEffect(ACEffectRegistry.IRRADIATED)) {
            return;
        }
        int leftHeight = VANILLA_GUI_HEIGHT;
        int width = guiGraphics.guiWidth();
        int height = guiGraphics.guiHeight();
        int health = Mth.ceil(player.getHealth());
        int forgeGuiTick = Minecraft.getInstance().gui.getGuiTicks();
        AttributeInstance attrMaxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        float healthMax = (float) attrMaxHealth.getValue();
        float absorb = Mth.ceil(player.getAbsorptionAmount());
        int healthRows = Mth.ceil((healthMax + absorb) / 2.0F / 10.0F);
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        ClientProxy.random.setSeed(forgeGuiTick * 312871L);
        int left = width / 2 - 91;
        int top = height - leftHeight;
        int regen = -1;
        if (player.hasEffect(MobEffects.REGENERATION)) {
            regen = forgeGuiTick % Mth.ceil(healthMax + 5.0F);
        }
        final int heartV = player.level().getLevelData().isHardcore() ? 9 : 0;
        int heartU = 0;
        float absorbRemaining = absorb;
        guiGraphics.pose().pushPose();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, POTION_EFFECT_HUD_OVERLAYS);
        for (int i = Mth.ceil((healthMax + absorb) / 2.0F) - 1; i >= 0; --i) {
            int row = Mth.ceil((float) (i + 1) / 10.0F) - 1;
            int x = left + i % 10 * 8;
            int y = top - row * rowHeight;
            if (health <= 4) {
                y += ClientProxy.random.nextInt(2);
            }
            if (i == regen) {
                y -= 2;
            }
            guiGraphics.blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU, heartV + 18, 9, 9, 32, 32);
            if (absorbRemaining > 0.0F) {
                if (absorbRemaining == absorb && absorb % 2.0F == 1.0F) {
                    guiGraphics.blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU + 9, heartV, 9, 9, 32, 32);
                    absorbRemaining -= 1.0F;
                } else {
                    guiGraphics.blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU, heartV, 9, 9, 32, 32);
                    absorbRemaining -= 2.0F;
                }
            } else {
                if (i * 2 + 1 < health) {
                    guiGraphics.blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU, heartV, 9, 9, 32, 32);
                } else if (i * 2 + 1 == health) {
                    guiGraphics.blit(POTION_EFFECT_HUD_OVERLAYS, x, y, 50, heartU + 9, heartV, 9, 9, 32, 32);
                }
            }
        }
        guiGraphics.pose().popPose();
    }

    /**
     * Custom third person arm poses, called from the start of HumanoidModel.poseRightArm/poseLeftArm
     * (Citadel's EventPosePlayerHand on NeoForge).
     * @return true if the vanilla arm pose should be skipped
     */
    public static boolean poseHumanoidArms(LivingEntity player, HumanoidModel<?> model) {
        boolean result = false;
        float f = Minecraft.getInstance().getTimer().getRealtimeDeltaTicks();
        float rightHandResistorShieldUseProgress = 0.0F;
        float leftHandResistorShieldUseProgress = 0.0F;
        float rightHandGalenaGauntletUseProgress = 0.0F;
        float leftHandGalenaGauntletUseProgress = 0.0F;
        float rightHandSpearUseProgress = 0.0F;
        float leftHandSpearUseProgress = 0.0F;
        float rightHandRaygunUseProgress = 0.0F;
        float leftHandRaygunUseProgress = 0.0F;
        boolean rightMain = player.getMainArm() == HumanoidArm.RIGHT;
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (mainHand.getItem() instanceof ResistorShieldItem) {
            if (rightMain) {
                rightHandResistorShieldUseProgress = Math.max(rightHandResistorShieldUseProgress, ResistorShieldItem.getLerpedUseTime(mainHand, f));
            } else {
                leftHandResistorShieldUseProgress = Math.max(leftHandResistorShieldUseProgress, ResistorShieldItem.getLerpedUseTime(mainHand, f));
            }
        }
        if (offHand.getItem() instanceof ResistorShieldItem) {
            if (rightMain) {
                leftHandResistorShieldUseProgress = Math.max(leftHandResistorShieldUseProgress, ResistorShieldItem.getLerpedUseTime(offHand, f));
            } else {
                rightHandResistorShieldUseProgress = Math.max(rightHandResistorShieldUseProgress, ResistorShieldItem.getLerpedUseTime(offHand, f));
            }
        }
        if (mainHand.getItem() instanceof GalenaGauntletItem) {
            if (rightMain) {
                rightHandGalenaGauntletUseProgress = Math.max(rightHandGalenaGauntletUseProgress, GalenaGauntletItem.getLerpedUseTime(mainHand, f));
            } else {
                leftHandGalenaGauntletUseProgress = Math.max(leftHandGalenaGauntletUseProgress, GalenaGauntletItem.getLerpedUseTime(mainHand, f));
            }
        }
        if (offHand.getItem() instanceof GalenaGauntletItem) {
            if (rightMain) {
                leftHandGalenaGauntletUseProgress = Math.max(leftHandGalenaGauntletUseProgress, GalenaGauntletItem.getLerpedUseTime(offHand, f));
            } else {
                rightHandGalenaGauntletUseProgress = Math.max(rightHandGalenaGauntletUseProgress, GalenaGauntletItem.getLerpedUseTime(offHand, f));
            }
        }
        if (mainHand.getItem() instanceof SpearItem && player.isUsingItem() && player.getUseItemRemainingTicks() > 0) {
            float f7 = (mainHand.getUseDuration(player) - ((float) player.getUseItemRemainingTicks() - f + 1.0F)) / 10.0F;
            if (rightMain) {
                rightHandSpearUseProgress = Math.max(rightHandSpearUseProgress, f7);
            } else {
                leftHandSpearUseProgress = Math.max(leftHandSpearUseProgress, f7);
            }
        }
        if (offHand.getItem() instanceof SpearItem && player.isUsingItem() && player.getUseItemRemainingTicks() > 0) {
            float f7 = (offHand.getUseDuration(player) - ((float) player.getUseItemRemainingTicks() - f + 1.0F)) / 10.0F;
            if (rightMain) {
                leftHandSpearUseProgress = Math.max(leftHandSpearUseProgress, f7);
            } else {
                rightHandSpearUseProgress = Math.max(rightHandSpearUseProgress, f7);
            }
        }
        if (mainHand.getItem() instanceof RaygunItem) {
            if (rightMain) {
                rightHandRaygunUseProgress = Math.max(rightHandRaygunUseProgress, RaygunItem.getLerpedUseTime(mainHand, f));
            } else {
                leftHandRaygunUseProgress = Math.max(leftHandRaygunUseProgress, RaygunItem.getLerpedUseTime(mainHand, f));
            }
        }
        if (offHand.getItem() instanceof RaygunItem) {
            if (rightMain) {
                leftHandRaygunUseProgress = Math.max(leftHandRaygunUseProgress, RaygunItem.getLerpedUseTime(offHand, f));
            } else {
                rightHandRaygunUseProgress = Math.max(rightHandRaygunUseProgress, RaygunItem.getLerpedUseTime(offHand, f));
            }
        }
        if (player.isPassenger() && player.getVehicle() instanceof SubterranodonEntity subterranodon) {
            float flight = subterranodon.getFlyProgress(f) - subterranodon.getHoverProgress(f);
            if (flight > 0.0F) {
                model.leftArm.xRot = -(float) Math.toRadians(180F) * flight;
                model.leftArm.zRot = (float) Math.toRadians(-10F) * flight;
                model.rightArm.xRot = -(float) Math.toRadians(180F) * flight;
                model.rightArm.zRot = (float) Math.toRadians(10F) * flight;
            }
            result = true;
        }
        if (leftHandResistorShieldUseProgress > 0.0F) {
            float useProgress = Math.min(10F, leftHandResistorShieldUseProgress) / 10F;
            float useProgressTurn = Math.min(useProgress * 4F, 1F);
            float useProgressUp = (float) Math.sin(useProgress * Math.PI);
            float armTilt = model.crouching ? 120F : 80F;
            model.leftArm.xRot = -(float) Math.toRadians(armTilt) - (float) Math.toRadians(80F) * useProgressUp;
            model.leftArm.yRot = (float) Math.toRadians(20F) * useProgressTurn;
            result = true;
        }
        if (rightHandResistorShieldUseProgress > 0.0F) {
            float useProgress = Math.min(10F, rightHandResistorShieldUseProgress) / 10F;
            float useProgressTurn = Math.min(useProgress * 4F, 1F);
            float useProgressUp = (float) Math.sin(useProgress * Math.PI);
            float armTilt = model.crouching ? 120F : 80F;
            model.rightArm.xRot = -(float) Math.toRadians(armTilt) - (float) Math.toRadians(80F) * useProgressUp;
            model.rightArm.yRot = -(float) Math.toRadians(20F) * useProgressTurn;
            result = true;
        }
        if (leftHandGalenaGauntletUseProgress > 0.0F) {
            float useProgress = Math.min(5F, leftHandGalenaGauntletUseProgress) / 5F;
            model.leftArm.xRot = (model.head.xRot - (float) Math.toRadians(80F)) * useProgress;
            model.leftArm.yRot = model.head.yRot * useProgress;
            result = true;
        }
        if (rightHandGalenaGauntletUseProgress > 0.0F) {
            float useProgress = Math.min(5F, rightHandGalenaGauntletUseProgress) / 5F;
            model.rightArm.xRot = (model.head.xRot - (float) Math.toRadians(80F)) * useProgress;
            model.rightArm.yRot = model.head.yRot * useProgress;
            result = true;
        }
        if (leftHandSpearUseProgress > 0.0F) {
            float useProgress = Math.min(1F, leftHandSpearUseProgress);
            float useProgressMiddle = (float) Math.sin(useProgress * Math.PI);
            model.leftArm.xRot = useProgress * ((float) Math.toRadians(-180F) + model.head.xRot);
            model.leftArm.yRot = useProgressMiddle * ((float) Math.toRadians(-25F) - model.head.yRot);
            model.leftArm.zRot = useProgress * (float) Math.toRadians(50F) - (float) Math.toRadians(25F);
            result = true;
        }
        if (rightHandSpearUseProgress > 0.0F) {
            float useProgress = Math.min(1F, rightHandSpearUseProgress);
            float useProgressMiddle = (float) Math.sin(useProgress * Math.PI);
            model.rightArm.xRot = useProgress * ((float) Math.toRadians(-180F) + model.head.xRot);
            model.rightArm.yRot = useProgressMiddle * ((float) Math.toRadians(25F) - model.head.yRot);
            model.rightArm.zRot = useProgress * -(float) Math.toRadians(50F) + (float) Math.toRadians(25F);
            result = true;
        }
        if (player.getVehicle() instanceof NuclearBombEntity) {
            float ageInTicks = player.tickCount + f;
            model.rightArm.xRot = (float) Math.toRadians(-170F);
            model.rightArm.yRot = (float) Math.toRadians(100F) + (float) Math.cos(ageInTicks * 0.35F) * (float) Math.toRadians(20F);
            model.rightArm.zRot = (float) Math.sin(ageInTicks * 0.35F) * (float) Math.toRadians(50F) - (float) Math.toRadians(70F);
            model.leftArm.yRot = (float) Math.toRadians(30F);
            result = true;
        }
        if (leftHandRaygunUseProgress > 0.0F) {
            float useProgress = Math.min(5F, leftHandRaygunUseProgress) / 5F;
            model.leftArm.xRot = (model.head.xRot - (float) Math.toRadians(80F)) * useProgress;
            model.leftArm.yRot = model.head.yRot * useProgress;
            model.leftArm.zRot = 0;
            result = true;
        }
        if (rightHandRaygunUseProgress > 0.0F) {
            float useProgress = Math.min(5F, rightHandRaygunUseProgress) / 5F;
            model.rightArm.xRot = (model.head.xRot - (float) Math.toRadians(80F)) * useProgress;
            model.rightArm.yRot = model.head.yRot * useProgress;
            model.rightArm.zRot = 0;
            result = true;
        }
        if (mainHand.getItem() instanceof ShotGumItem && ShotGumItem.shouldBeHeldUpright(mainHand)) {
            poseShotGum(model, rightMain);
            result = true;
        }
        if (offHand.getItem() instanceof ShotGumItem && ShotGumItem.shouldBeHeldUpright(offHand)) {
            poseShotGum(model, !rightMain);
            result = true;
        }
        if (mainHand.getItem() instanceof CandyCaneHookItem && CandyCaneHookItem.isActive(mainHand)
                && offHand.getItem() instanceof CandyCaneHookItem && CandyCaneHookItem.isActive(offHand)
                && player.getVehicle() instanceof GumWormSegmentEntity) {
            float rightWiggle = -Math.min(player.xxa, 0F) * (float) Math.sin(player.tickCount + AlexsCaves.PROXY.getPartialTicks()) * 25;
            float leftWiggle = Math.max(player.xxa, 0F) * (float) Math.sin(player.tickCount + AlexsCaves.PROXY.getPartialTicks()) * 25;
            model.rightArm.xRot = (float) Math.toRadians(-100F + rightWiggle);
            model.leftArm.xRot = (float) Math.toRadians(-100F + leftWiggle);
            model.rightArm.yRot = (float) Math.toRadians(20F);
            model.leftArm.yRot = (float) Math.toRadians(-20F);
            model.rightLeg.xRot = (float) Math.toRadians(-20F);
            model.leftLeg.xRot = (float) Math.toRadians(20F);
            result = true;
        }
        if (!result && player.hasEffect(ACEffectRegistry.SUGAR_RUSH) && !AlexsCaves.PROXY.isFirstPersonPlayer(player)) {
            float speedModifier = 0.35F;
            if (AlexsCaves.COMMON_CONFIG.sugarRushSlowsTime.get() && AlexsCaves.PROXY.isTickRateModificationActive(Minecraft.getInstance().level)) {
                float tickRate = ClientTickRateTracker.getForClient(Minecraft.getInstance()).getClientTickRate() / 50.0F;
                speedModifier *= tickRate;
            }
            float deltaSpeed = 1.0F;
            float partialTicks = AlexsCaves.PROXY.getPartialTicks();
            float walkPos = player.walkAnimation.position(partialTicks);
            float walkSpeed = player.walkAnimation.speed(partialTicks);
            float headXRot = player.getViewXRot(partialTicks);
            float headYRot = Mth.lerp(partialTicks, player.yHeadRotO, player.yHeadRot) - Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot);
            model.rightArm.xRot = Mth.cos(walkPos * speedModifier + (float) Math.PI * 0.5F) * 2.0F * walkSpeed * 0.5F / deltaSpeed;
            model.leftArm.xRot = Mth.cos(walkPos * speedModifier) * 2.0F * walkSpeed * 0.5F / deltaSpeed;
            model.rightArm.zRot = (Mth.sin(walkPos * -speedModifier + (float) Math.PI * 0.5F) + 2.5F) * 1.5F * walkSpeed * 0.5F / deltaSpeed;
            model.leftArm.zRot = (Mth.sin(walkPos * -speedModifier) - 2.5F) * 1.5F * walkSpeed * 0.5F / deltaSpeed;
            model.head.xRot = headXRot * ((float) Math.PI / 180F) + Mth.cos(walkPos * speedModifier + (float) Math.PI) * 1.0F * walkSpeed * 0.5F / deltaSpeed;
            model.head.yRot = headYRot * ((float) Math.PI / 180F) + Mth.sin(walkPos * speedModifier + (float) Math.PI) * 1.0F * walkSpeed * 0.5F / deltaSpeed;
            model.leftLeg.xRot = Mth.cos(walkPos * speedModifier + (float) Math.PI) * 4.0F * walkSpeed * 0.5F / deltaSpeed;
            model.rightLeg.xRot = Mth.cos(walkPos * speedModifier) * 4.0F * walkSpeed * 0.5F / deltaSpeed;
            result = true;
        }
        return result;
    }

    private static void poseShotGum(HumanoidModel<?> model, boolean rightArmHolds) {
        if (rightArmHolds) {
            model.rightArm.xRot = (model.head.xRot - (float) Math.toRadians(70F));
            model.rightArm.yRot = model.head.yRot;
            model.rightArm.zRot = 0;
            model.leftArm.xRot = model.head.xRot - (float) Math.toRadians(70F);
            model.leftArm.yRot = model.head.yRot + (float) Math.toRadians(40F);
            model.leftArm.zRot = (float) Math.toRadians(20F);
        } else {
            model.leftArm.xRot = (model.head.xRot - (float) Math.toRadians(70F));
            model.leftArm.yRot = model.head.yRot;
            model.leftArm.zRot = 0;
            model.rightArm.xRot = model.head.xRot - (float) Math.toRadians(70F);
            model.rightArm.yRot = model.head.yRot + (float) Math.toRadians(-40F);
            model.rightArm.zRot = (float) Math.toRadians(-20F);
        }
    }
}
