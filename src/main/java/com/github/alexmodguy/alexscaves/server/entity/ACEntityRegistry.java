package com.github.alexmodguy.alexscaves.server.entity;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.block.fluid.ACFluidRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.*;
import com.github.alexmodguy.alexscaves.server.entity.living.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = AlexsCaves.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ACEntityRegistry {


    public static final DeferredRegister<EntityType<?>> DEF_REG = DeferredRegister.create(Registries.ENTITY_TYPE, AlexsCaves.MODID);
    // Custom MobCategory values appended by MobCategoryMixin (Fabric has no enum extension support).
    public static final MobCategory CAVE_CREATURE = ACMobCategories.caveCreature();
    public static final MobCategory DEEP_SEA_CREATURE = ACMobCategories.deepSeaCreature();
    public static final DeferredHolder<EntityType<?>, EntityType<AlexsCavesBoatEntity>> BOAT = DEF_REG.register("boat", () -> (EntityType) EntityType.Builder.of(AlexsCavesBoatEntity::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(entityId("boat")));
    public static final DeferredHolder<EntityType<?>, EntityType<AlexsCavesChestBoatEntity>> CHEST_BOAT = DEF_REG.register("chest_boat", () -> (EntityType) EntityType.Builder.of(AlexsCavesChestBoatEntity::new, MobCategory.MISC).sized(1.375F, 0.5625F).clientTrackingRange(10).build(entityId("chest_boat")));
    public static final DeferredHolder<EntityType<?>, EntityType<MovingMetalBlockEntity>> MOVING_METAL_BLOCK = DEF_REG.register("moving_metal_block", () -> (EntityType) EntityType.Builder.of(MovingMetalBlockEntity::new, MobCategory.MISC).sized(0.99F, 0.99F).updateInterval(1).updateInterval(10).clientTrackingRange(20).build(entityId("moving_metal_block")));
    public static final DeferredHolder<EntityType<?>, EntityType<TeletorEntity>> TELETOR = DEF_REG.register("teletor", () -> (EntityType) EntityType.Builder.of(TeletorEntity::new, MobCategory.MONSTER).sized(0.99F, 1.99F).build(entityId("teletor")));
    public static final DeferredHolder<EntityType<?>, EntityType<MagneticWeaponEntity>> MAGNETIC_WEAPON = DEF_REG.register("magnetic_weapon", () -> (EntityType) EntityType.Builder.of(MagneticWeaponEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).clientTrackingRange(20).build(entityId("magnetic_weapon")));
    public static final DeferredHolder<EntityType<?>, EntityType<MagnetronEntity>> MAGNETRON = DEF_REG.register("magnetron", () -> (EntityType) EntityType.Builder.of(MagnetronEntity::new, MobCategory.MONSTER).sized(0.8F, 2.0F).build(entityId("magnetron")));
    public static final DeferredHolder<EntityType<?>, EntityType<BoundroidEntity>> BOUNDROID = DEF_REG.register("boundroid", () -> (EntityType) EntityType.Builder.of(BoundroidEntity::new, MobCategory.MONSTER).sized(1.4F, 0.75F).build(entityId("boundroid")));
    public static final DeferredHolder<EntityType<?>, EntityType<BoundroidWinchEntity>> BOUNDROID_WINCH = DEF_REG.register("boundroid_winch", () -> (EntityType) EntityType.Builder.of(BoundroidWinchEntity::new, MobCategory.MONSTER).sized(0.8F, 1.4F).build(entityId("boundroid_winch")));
    public static final DeferredHolder<EntityType<?>, EntityType<FerrouslimeEntity>> FERROUSLIME = DEF_REG.register("ferrouslime", () -> (EntityType) EntityType.Builder.of(FerrouslimeEntity::new, MobCategory.MONSTER).sized(0.85F, 0.85F).build(entityId("ferrouslime")));
    public static final DeferredHolder<EntityType<?>, EntityType<NotorEntity>> NOTOR = DEF_REG.register("notor", () -> (EntityType) EntityType.Builder.of(NotorEntity::new, MobCategory.AMBIENT).sized(0.5F, 0.65F).build(entityId("notor")));
    public static final DeferredHolder<EntityType<?>, EntityType<QuarrySmasherEntity>> QUARRY_SMASHER = DEF_REG.register("quarry_smasher", () -> (EntityType) EntityType.Builder.of(QuarrySmasherEntity::new, MobCategory.MISC).sized(0.9F, 1.2F).updateInterval(1).updateInterval(10).clientTrackingRange(20).build(entityId("quarry_smasher")));
    public static final DeferredHolder<EntityType<?>, EntityType<SeekingArrowEntity>> SEEKING_ARROW = DEF_REG.register("seeking_arrow", () -> (EntityType) EntityType.Builder.of(SeekingArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(entityId("seeking_arrow")));
    public static final DeferredHolder<EntityType<?>, EntityType<SubterranodonEntity>> SUBTERRANODON = DEF_REG.register("subterranodon", () -> (EntityType) EntityType.Builder.of(SubterranodonEntity::new, CAVE_CREATURE).sized(1.75F, 1.2F).clientTrackingRange(12).updateInterval(1).build(entityId("subterranodon")));
    public static final DeferredHolder<EntityType<?>, EntityType<VallumraptorEntity>> VALLUMRAPTOR = DEF_REG.register("vallumraptor", () -> (EntityType) EntityType.Builder.of(VallumraptorEntity::new, CAVE_CREATURE).sized(0.8F, 1.5F).clientTrackingRange(8).build(entityId("vallumraptor")));
    public static final DeferredHolder<EntityType<?>, EntityType<GrottoceratopsEntity>> GROTTOCERATOPS = DEF_REG.register("grottoceratops", () -> (EntityType) EntityType.Builder.of(GrottoceratopsEntity::new, CAVE_CREATURE).sized(2.3F, 2.5F).clientTrackingRange(8).build(entityId("grottoceratops")));
    public static final DeferredHolder<EntityType<?>, EntityType<TrilocarisEntity>> TRILOCARIS = DEF_REG.register("trilocaris", () -> (EntityType) EntityType.Builder.of(TrilocarisEntity::new, MobCategory.WATER_AMBIENT).sized(0.9F, 0.4F).build(entityId("trilocaris")));
    public static final DeferredHolder<EntityType<?>, EntityType<TremorsaurusEntity>> TREMORSAURUS = DEF_REG.register("tremorsaurus", () -> (EntityType) EntityType.Builder.of(TremorsaurusEntity::new, CAVE_CREATURE).sized(2.5F, 3.85F).clientTrackingRange(8).build(entityId("tremorsaurus")));
    public static final DeferredHolder<EntityType<?>, EntityType<RelicheirusEntity>> RELICHEIRUS = DEF_REG.register("relicheirus", () -> (EntityType) EntityType.Builder.of(RelicheirusEntity::new, CAVE_CREATURE).sized(2.65F, 5.9F).clientTrackingRange(9).build(entityId("relicheirus")));
    public static final DeferredHolder<EntityType<?>, EntityType<LuxtructosaurusEntity>> LUXTRUCTOSAURUS = DEF_REG.register("luxtructosaurus", () -> (EntityType) EntityType.Builder.of(LuxtructosaurusEntity::new, MobCategory.MONSTER).sized(6.0F, 8.5F).clientTrackingRange(12).fireImmune().build(entityId("luxtructosaurus")));
    public static final DeferredHolder<EntityType<?>, EntityType<TephraEntity>> TEPHRA = DEF_REG.register("tephra", () -> (EntityType) EntityType.Builder.of(TephraEntity::new, MobCategory.MISC).sized(0.6F, 0.6F).fireImmune().updateInterval(1).build(entityId("tephra")));
    public static final DeferredHolder<EntityType<?>, EntityType<AtlatitanEntity>> ATLATITAN = DEF_REG.register("atlatitan", () -> (EntityType) EntityType.Builder.of(AtlatitanEntity::new, CAVE_CREATURE).sized(5.0F, 8.0F).clientTrackingRange(11).build(entityId("atlatitan")));
    public static final DeferredHolder<EntityType<?>, EntityType<FallingTreeBlockEntity>> FALLING_TREE_BLOCK = DEF_REG.register("falling_tree_block", () -> (EntityType) EntityType.Builder.of(FallingTreeBlockEntity::new, MobCategory.MISC).sized(0.99F, 0.99F).updateInterval(1).updateInterval(10).clientTrackingRange(20).build(entityId("falling_tree_block")));
    public static final DeferredHolder<EntityType<?>, EntityType<CrushedBlockEntity>> CRUSHED_BLOCK = DEF_REG.register("crushed_block", () -> (EntityType) EntityType.Builder.of(CrushedBlockEntity::new, MobCategory.MISC).sized(0.99F, 0.99F).updateInterval(1).updateInterval(10).clientTrackingRange(20).build(entityId("crushed_block")));
    public static final DeferredHolder<EntityType<?>, EntityType<LimestoneSpearEntity>> LIMESTONE_SPEAR = DEF_REG.register("limestone_spear", () -> (EntityType) EntityType.Builder.of(LimestoneSpearEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(entityId("limestone_spear")));
    public static final DeferredHolder<EntityType<?>, EntityType<ExtinctionSpearEntity>> EXTINCTION_SPEAR = DEF_REG.register("extinction_spear", () -> (EntityType) EntityType.Builder.of(ExtinctionSpearEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).fireImmune().build(entityId("extinction_spear")));
    public static final DeferredHolder<EntityType<?>, EntityType<DinosaurSpiritEntity>> DINOSAUR_SPIRIT = DEF_REG.register("dinosaur_spirit", () -> (EntityType) EntityType.Builder.of(DinosaurSpiritEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).updateInterval(1).fireImmune().build(entityId("dinosaur_spirit")));
    public static final DeferredHolder<EntityType<?>, EntityType<NuclearExplosionEntity>> NUCLEAR_EXPLOSION = DEF_REG.register("nuclear_explosion", () -> (EntityType) EntityType.Builder.of(NuclearExplosionEntity::new, MobCategory.MISC).sized(0.99F, 0.99F).updateInterval(1).updateInterval(10).clientTrackingRange(20).build(entityId("nuclear_explosion")));
    public static final DeferredHolder<EntityType<?>, EntityType<NuclearBombEntity>> NUCLEAR_BOMB = DEF_REG.register("nuclear_bomb", () -> (EntityType) EntityType.Builder.of(NuclearBombEntity::new, MobCategory.MISC).sized(0.98F, 0.98F).updateInterval(1).updateInterval(10).clientTrackingRange(20).build(entityId("nuclear_bomb")));
    public static final DeferredHolder<EntityType<?>, EntityType<NucleeperEntity>> NUCLEEPER = DEF_REG.register("nucleeper", () -> (EntityType) EntityType.Builder.of(NucleeperEntity::new, MobCategory.MONSTER).sized(0.98F, 3.95F).build(entityId("nucleeper")));
    public static final DeferredHolder<EntityType<?>, EntityType<RadgillEntity>> RADGILL = DEF_REG.register("radgill", () -> (EntityType) EntityType.Builder.of(RadgillEntity::new, MobCategory.WATER_AMBIENT).sized(0.9F, 0.6F).build(entityId("radgill")));
    public static final DeferredHolder<EntityType<?>, EntityType<BrainiacEntity>> BRAINIAC = DEF_REG.register("brainiac", () -> (EntityType) EntityType.Builder.of(BrainiacEntity::new, MobCategory.MONSTER).sized(1.3F, 2.5F).build(entityId("brainiac")));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownWasteDrumEntity>> THROWN_WASTE_DRUM = DEF_REG.register("thrown_waste_drum", () -> (EntityType) EntityType.Builder.of(ThrownWasteDrumEntity::new, MobCategory.MISC).sized(0.98F, 0.98F).updateInterval(1).updateInterval(10).clientTrackingRange(20).build(entityId("thrown_waste_drum")));
    public static final DeferredHolder<EntityType<?>, EntityType<GammaroachEntity>> GAMMAROACH = DEF_REG.register("gammaroach", () -> (EntityType) EntityType.Builder.of(GammaroachEntity::new, MobCategory.AMBIENT).sized(1.25F, 0.9F).build(entityId("gammaroach")));
    public static final DeferredHolder<EntityType<?>, EntityType<RaycatEntity>> RAYCAT = DEF_REG.register("raycat", () -> (EntityType) EntityType.Builder.of(RaycatEntity::new, CAVE_CREATURE).sized(0.85F, 0.6F).build(entityId("raycat")));
    public static final DeferredHolder<EntityType<?>, EntityType<CinderBrickEntity>> CINDER_BRICK = DEF_REG.register("cinder_brick", () -> (EntityType) EntityType.Builder.of(CinderBrickEntity::new, MobCategory.MISC).sized(0.4F, 0.4F).updateInterval(1).build(entityId("cinder_brick")));
    public static final DeferredHolder<EntityType<?>, EntityType<TremorzillaEntity>> TREMORZILLA = DEF_REG.register("tremorzilla", () -> (EntityType) EntityType.Builder.of(TremorzillaEntity::new, CAVE_CREATURE).sized(4.5F, 11F).clientTrackingRange(11).fireImmune().build(entityId("tremorzilla")));
    public static final DeferredHolder<EntityType<?>, EntityType<LanternfishEntity>> LANTERNFISH = DEF_REG.register("lanternfish", () -> (EntityType) EntityType.Builder.of(LanternfishEntity::new, MobCategory.WATER_AMBIENT).sized(0.5F, 0.4F).build(entityId("lanternfish")));
    public static final DeferredHolder<EntityType<?>, EntityType<SeaPigEntity>> SEA_PIG = DEF_REG.register("sea_pig", () -> (EntityType) EntityType.Builder.of(SeaPigEntity::new, DEEP_SEA_CREATURE).sized(0.5F, 0.65F).build(entityId("sea_pig")));
    public static final DeferredHolder<EntityType<?>, EntityType<SubmarineEntity>> SUBMARINE = DEF_REG.register("submarine", () -> (EntityType) EntityType.Builder.of(SubmarineEntity::new, MobCategory.MISC).sized(3.5F, 3.3F).updateInterval(1).updateInterval(10).clientTrackingRange(20).build(entityId("submarine")));
    public static final DeferredHolder<EntityType<?>, EntityType<HullbreakerEntity>> HULLBREAKER = DEF_REG.register("hullbreaker", () -> (EntityType) EntityType.Builder.of(HullbreakerEntity::new, MobCategory.UNDERGROUND_WATER_CREATURE).sized(4.65F, 4.5F).clientTrackingRange(20).build(entityId("hullbreaker")));
    public static final DeferredHolder<EntityType<?>, EntityType<GossamerWormEntity>> GOSSAMER_WORM = DEF_REG.register("gossamer_worm", () -> (EntityType) EntityType.Builder.of(GossamerWormEntity::new, DEEP_SEA_CREATURE).sized(1.15F, 0.5F).build(entityId("gossamer_worm")));
    public static final DeferredHolder<EntityType<?>, EntityType<TripodfishEntity>> TRIPODFISH = DEF_REG.register("tripodfish", () -> (EntityType) EntityType.Builder.of(TripodfishEntity::new, DEEP_SEA_CREATURE).sized(0.95F, 0.5F).build(entityId("tripodfish")));
    public static final DeferredHolder<EntityType<?>, EntityType<DeepOneEntity>> DEEP_ONE = DEF_REG.register("deep_one", () -> (EntityType) EntityType.Builder.of(DeepOneEntity::new, MobCategory.MONSTER).sized(0.9F, 2.2F).clientTrackingRange(12).updateInterval(1).build(entityId("deep_one")));
    public static final DeferredHolder<EntityType<?>, EntityType<InkBombEntity>> INK_BOMB = DEF_REG.register("ink_bomb", () -> (EntityType) EntityType.Builder.of(InkBombEntity::new, MobCategory.MISC).sized(0.6F, 0.6F).updateInterval(1).build(entityId("ink_bomb")));
    public static final DeferredHolder<EntityType<?>, EntityType<DeepOneKnightEntity>> DEEP_ONE_KNIGHT = DEF_REG.register("deep_one_knight", () -> (EntityType) EntityType.Builder.of(DeepOneKnightEntity::new, MobCategory.MONSTER).sized(1.15F, 2.5F).clientTrackingRange(12).updateInterval(1).build(entityId("deep_one_knight")));
    public static final DeferredHolder<EntityType<?>, EntityType<DeepOneMageEntity>> DEEP_ONE_MAGE = DEF_REG.register("deep_one_mage", () -> (EntityType) EntityType.Builder.of(DeepOneMageEntity::new, MobCategory.MONSTER).sized(1.35F, 2.5F).clientTrackingRange(12).updateInterval(1).build(entityId("deep_one_mage")));
    public static final DeferredHolder<EntityType<?>, EntityType<WaterBoltEntity>> WATER_BOLT = DEF_REG.register("water_bolt", () -> (EntityType) EntityType.Builder.of(WaterBoltEntity::new, MobCategory.MISC).sized(0.6F, 0.6F).updateInterval(1).build(entityId("water_bolt")));
    public static final DeferredHolder<EntityType<?>, EntityType<WaveEntity>> WAVE = DEF_REG.register("wave", () -> (EntityType) EntityType.Builder.of(WaveEntity::new, MobCategory.MISC).sized(0.9F, 0.9F).updateInterval(1).build(entityId("wave")));
    public static final DeferredHolder<EntityType<?>, EntityType<MineGuardianEntity>> MINE_GUARDIAN = DEF_REG.register("mine_guardian", () -> (EntityType) EntityType.Builder.of(MineGuardianEntity::new, MobCategory.MONSTER).sized(1.3F, 1.3F).updateInterval(1).build(entityId("mine_guardian")));
    public static final DeferredHolder<EntityType<?>, EntityType<MineGuardianAnchorEntity>> MINE_GUARDIAN_ANCHOR = DEF_REG.register("mine_guardian_anchor", () -> (EntityType) EntityType.Builder.of(MineGuardianAnchorEntity::new, MobCategory.MISC).sized(0.6F, 1.35F).build(entityId("mine_guardian_anchor")));
    public static final DeferredHolder<EntityType<?>, EntityType<DepthChargeEntity>> DEPTH_CHARGE = DEF_REG.register("depth_charge", () -> (EntityType) EntityType.Builder.of(DepthChargeEntity::new, MobCategory.MISC).sized(0.7F, 0.7F).updateInterval(1).build(entityId("depth_charge")));
    public static final DeferredHolder<EntityType<?>, EntityType<FloaterEntity>> FLOATER = DEF_REG.register("floater", () -> (EntityType) EntityType.Builder.of(FloaterEntity::new, MobCategory.MISC).sized(0.98F, 0.98F).updateInterval(1).updateInterval(10).clientTrackingRange(20).build(entityId("floater")));
    public static final DeferredHolder<EntityType<?>, EntityType<GuanoEntity>> GUANO = DEF_REG.register("guano", () -> (EntityType) EntityType.Builder.of(GuanoEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(entityId("guano")));
    public static final DeferredHolder<EntityType<?>, EntityType<FallingGuanoEntity>> FALLING_GUANO = DEF_REG.register("falling_guano", () -> (EntityType) EntityType.Builder.of(FallingGuanoEntity::new, MobCategory.MISC).sized(0.8F, 0.9F).updateInterval(1).updateInterval(10).clientTrackingRange(20).build(entityId("falling_guano")));
    public static final DeferredHolder<EntityType<?>, EntityType<GloomothEntity>> GLOOMOTH = DEF_REG.register("gloomoth", () -> (EntityType) EntityType.Builder.of(GloomothEntity::new, MobCategory.AMBIENT).sized(0.99F, 0.99F).clientTrackingRange(12).updateInterval(1).build(entityId("gloomoth")));
    public static final DeferredHolder<EntityType<?>, EntityType<UnderzealotEntity>> UNDERZEALOT = DEF_REG.register("underzealot", () -> (EntityType) EntityType.Builder.of(UnderzealotEntity::new, MobCategory.MONSTER).sized(0.8F, 1.2F).build(entityId("underzealot")));
    public static final DeferredHolder<EntityType<?>, EntityType<WatcherEntity>> WATCHER = DEF_REG.register("watcher", () -> (EntityType) EntityType.Builder.of(WatcherEntity::new, MobCategory.MONSTER).sized(0.9F, 1.9F).build(entityId("watcher")));
    public static final DeferredHolder<EntityType<?>, EntityType<CorrodentEntity>> CORRODENT = DEF_REG.register("corrodent", () -> (EntityType) EntityType.Builder.of(CorrodentEntity::new, MobCategory.MONSTER).sized(0.9F, 0.9F).build(entityId("corrodent")));
    public static final DeferredHolder<EntityType<?>, EntityType<VesperEntity>> VESPER = DEF_REG.register("vesper", () -> (EntityType) EntityType.Builder.of(VesperEntity::new, MobCategory.MONSTER).sized(1.2F, 1.65F).clientTrackingRange(12).updateInterval(1).build(entityId("vesper")));
    public static final DeferredHolder<EntityType<?>, EntityType<ForsakenEntity>> FORSAKEN = DEF_REG.register("forsaken", () -> (EntityType) EntityType.Builder.of(ForsakenEntity::new, MobCategory.MONSTER).sized(3F, 3.5F).clientTrackingRange(12).updateInterval(1).build(entityId("forsaken")));
    public static final DeferredHolder<EntityType<?>, EntityType<BeholderEyeEntity>> BEHOLDER_EYE = DEF_REG.register("beholder_eye", () -> (EntityType) EntityType.Builder.of(BeholderEyeEntity::new, MobCategory.MISC).sized(0.3F, 0.3F).build(entityId("beholder_eye")));
    public static final DeferredHolder<EntityType<?>, EntityType<DesolateDaggerEntity>> DESOLATE_DAGGER = DEF_REG.register("desolate_dagger", () -> (EntityType) EntityType.Builder.of(DesolateDaggerEntity::new, MobCategory.MISC).sized(0.6F, 0.6F).updateInterval(1).build(entityId("desolate_dagger")));
    public static final DeferredHolder<EntityType<?>, EntityType<BurrowingArrowEntity>> BURROWING_ARROW = DEF_REG.register("burrowing_arrow", () -> (EntityType) EntityType.Builder.of(BurrowingArrowEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(entityId("burrowing_arrow")));
    public static final DeferredHolder<EntityType<?>, EntityType<DarkArrowEntity>> DARK_ARROW = DEF_REG.register("dark_arrow", () -> (EntityType) EntityType.Builder.of(DarkArrowEntity::new, MobCategory.MISC).sized(1.1F, 0.5F).updateInterval(1).build(entityId("dark_arrow")));
    public static final DeferredHolder<EntityType<?>, EntityType<SweetishFishEntity>> SWEETISH_FISH = DEF_REG.register("sweetish_fish", () -> (EntityType) EntityType.Builder.of(SweetishFishEntity::new, MobCategory.WATER_AMBIENT).sized(0.7F, 0.45F).build(entityId("sweetish_fish")));
    public static final DeferredHolder<EntityType<?>, EntityType<CaniacEntity>> CANIAC = DEF_REG.register("caniac", () -> (EntityType) EntityType.Builder.of(CaniacEntity::new, MobCategory.MONSTER).sized(0.9F, 2.3F).clientTrackingRange(12).updateInterval(1).build(entityId("caniac")));
    public static final DeferredHolder<EntityType<?>, EntityType<GumbeeperEntity>> GUMBEEPER = DEF_REG.register("gumbeeper", () -> (EntityType) EntityType.Builder.of(GumbeeperEntity::new, MobCategory.MONSTER).sized(0.8F, 1.6F).build(entityId("gumbeeper")));
    public static final DeferredHolder<EntityType<?>, EntityType<GumballEntity>> GUMBALL = DEF_REG.register("gumball", () -> (EntityType) EntityType.Builder.of(GumballEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).updateInterval(1).build(entityId("gumball")));
    public static final DeferredHolder<EntityType<?>, EntityType<CandicornEntity>> CANDICORN = DEF_REG.register("candicorn", () -> (EntityType) EntityType.Builder.of(CandicornEntity::new, CAVE_CREATURE).sized(1.7F, 2.25F).updateInterval(1).build(entityId("candicorn")));
    public static final DeferredHolder<EntityType<?>, EntityType<GumWormEntity>> GUM_WORM = DEF_REG.register("gum_worm", () -> (EntityType) EntityType.Builder.of(GumWormEntity::new, MobCategory.MONSTER).sized(3.5F, 2.7F).clientTrackingRange(14).updateInterval(1).fireImmune().build(entityId("gum_worm")));
    public static final DeferredHolder<EntityType<?>, EntityType<GumWormSegmentEntity>> GUM_WORM_SEGMENT = DEF_REG.register("gum_worm_segment", () -> (EntityType) EntityType.Builder.of(GumWormSegmentEntity::new, MobCategory.MISC).sized(2.0F, 2.0F).clientTrackingRange(14).updateInterval(1).fireImmune().build(entityId("gum_worm_segment")));
    public static final DeferredHolder<EntityType<?>, EntityType<CaramelCubeEntity>> CARAMEL_CUBE = DEF_REG.register("caramel_cube", () -> (EntityType) EntityType.Builder.of(CaramelCubeEntity::new, MobCategory.MONSTER).sized(0.8F, 0.8F).build(entityId("caramel_cube")));
    public static final DeferredHolder<EntityType<?>, EntityType<MeltedCaramelEntity>> MELTED_CARAMEL = DEF_REG.register("melted_caramel", () -> (EntityType) EntityType.Builder.of(MeltedCaramelEntity::new, MobCategory.MISC).sized(0.99F, 0.1F).build(entityId("melted_caramel")));
    public static final DeferredHolder<EntityType<?>, EntityType<GummyBearEntity>> GUMMY_BEAR = DEF_REG.register("gummy_bear", () -> (EntityType) EntityType.Builder.of(GummyBearEntity::new, CAVE_CREATURE).sized(1.45F, 1.2F).build(entityId("gummy_bear")));
    public static final DeferredHolder<EntityType<?>, EntityType<LicowitchEntity>> LICOWITCH = DEF_REG.register("licowitch", () -> (EntityType) EntityType.Builder.of(LicowitchEntity::new, MobCategory.MONSTER).sized(0.8F, 1.9F).build(entityId("licowitch")));
    public static final DeferredHolder<EntityType<?>, EntityType<SpinningPeppermintEntity>> SPINNING_PEPPERMINT = DEF_REG.register("spinning_peppermint", () -> (EntityType) EntityType.Builder.of(SpinningPeppermintEntity::new, MobCategory.MISC).sized(0.8F, 0.4F).build(entityId("spinning_peppermint")));
    public static final DeferredHolder<EntityType<?>, EntityType<SugarStaffHexEntity>> SUGAR_STAFF_HEX = DEF_REG.register("sugar_staff_hex", () -> (EntityType) EntityType.Builder.of(SugarStaffHexEntity::new, MobCategory.MISC).sized(4.0F, 0.25F).build(entityId("sugar_staff_hex")));
    public static final DeferredHolder<EntityType<?>, EntityType<GingerbreadManEntity>> GINGERBREAD_MAN = DEF_REG.register("gingerbread_man", () -> (EntityType) EntityType.Builder.of(GingerbreadManEntity::new, MobCategory.MONSTER).sized(0.5F, 0.9F).build(entityId("gingerbread_man")));
    public static final DeferredHolder<EntityType<?>, EntityType<ThrownIceCreamScoopEntity>> THROWN_ICE_CREAM_SCOOP = DEF_REG.register("thrown_ice_cream_scoop", () -> (EntityType) EntityType.Builder.of(ThrownIceCreamScoopEntity::new, MobCategory.MISC).sized(0.4F, 0.4F).updateInterval(1).build(entityId("thrown_ice_cream_scoop")));
    public static final DeferredHolder<EntityType<?>, EntityType<FallingFrostmintEntity>> FALLING_FROSTMINT = DEF_REG.register("falling_frostmint", () -> (EntityType) EntityType.Builder.of(FallingFrostmintEntity::new, MobCategory.MISC).sized(0.8F, 0.9F).updateInterval(1).updateInterval(10).clientTrackingRange(20).build(entityId("falling_frostmint")));
    public static final DeferredHolder<EntityType<?>, EntityType<CandyCaneHookEntity>> CANDY_CANE_HOOK = DEF_REG.register("candy_cane_hook", () -> (EntityType) EntityType.Builder.of(CandyCaneHookEntity::new, MobCategory.MISC).sized(0.6F, 0.6F).updateInterval(1).updateInterval(10).clientTrackingRange(20).build(entityId("candy_cane_hook")));
    public static final DeferredHolder<EntityType<?>, EntityType<SodaBottleRocketEntity>> SODA_BOTTLE_ROCKET = DEF_REG.register("soda_bottle_rocket", () -> (EntityType) EntityType.Builder.of(SodaBottleRocketEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(entityId("soda_bottle_rocket")));
    public static final DeferredHolder<EntityType<?>, EntityType<FrostmintSpearEntity>> FROSTMINT_SPEAR = DEF_REG.register("frostmint_spear", () -> (EntityType) EntityType.Builder.of(FrostmintSpearEntity::new, MobCategory.MISC).sized(0.5F, 0.5F).updateInterval(1).build(entityId("frostmint_spear")));

    private static String entityId(String path) {
        return AlexsCaves.MODID + ":" + path;
    }

    @SubscribeEvent
    public static void initializeAttributes(EntityAttributeCreationEvent event) {
        event.put(TELETOR.get(), TeletorEntity.createAttributes().build());
        event.put(MAGNETRON.get(), MagnetronEntity.createAttributes().build());
        event.put(BOUNDROID.get(), BoundroidEntity.createAttributes().build());
        event.put(BOUNDROID_WINCH.get(), BoundroidEntity.createAttributes().build());
        event.put(FERROUSLIME.get(), FerrouslimeEntity.createAttributes().build());
        event.put(NOTOR.get(), NotorEntity.createAttributes().build());
        event.put(SUBTERRANODON.get(), SubterranodonEntity.createAttributes().build());
        event.put(VALLUMRAPTOR.get(), VallumraptorEntity.createAttributes().build());
        event.put(GROTTOCERATOPS.get(), GrottoceratopsEntity.createAttributes().build());
        event.put(TRILOCARIS.get(), TrilocarisEntity.createAttributes().build());
        event.put(TREMORSAURUS.get(), TremorsaurusEntity.createAttributes().build());
        event.put(RELICHEIRUS.get(), RelicheirusEntity.createAttributes().build());
        event.put(LUXTRUCTOSAURUS.get(), LuxtructosaurusEntity.createAttributes().build());
        event.put(ATLATITAN.get(), AtlatitanEntity.createAttributes().build());
        event.put(NUCLEEPER.get(), NucleeperEntity.createAttributes().build());
        event.put(RADGILL.get(), RadgillEntity.createAttributes().build());
        event.put(BRAINIAC.get(), BrainiacEntity.createAttributes().build());
        event.put(GAMMAROACH.get(), GammaroachEntity.createAttributes().build());
        event.put(RAYCAT.get(), RaycatEntity.createAttributes().build());
        event.put(TREMORZILLA.get(), TremorzillaEntity.createAttributes().build());
        event.put(LANTERNFISH.get(), LanternfishEntity.createAttributes().build());
        event.put(SEA_PIG.get(), SeaPigEntity.createAttributes().build());
        event.put(HULLBREAKER.get(), HullbreakerEntity.createAttributes().build());
        event.put(GOSSAMER_WORM.get(), GossamerWormEntity.createAttributes().build());
        event.put(TRIPODFISH.get(), TripodfishEntity.createAttributes().build());
        event.put(DEEP_ONE.get(), DeepOneEntity.createAttributes().build());
        event.put(DEEP_ONE_KNIGHT.get(), DeepOneKnightEntity.createAttributes().build());
        event.put(DEEP_ONE_MAGE.get(), DeepOneMageEntity.createAttributes().build());
        event.put(MINE_GUARDIAN.get(), MineGuardianEntity.createAttributes().build());
        event.put(GLOOMOTH.get(), GloomothEntity.createAttributes().build());
        event.put(UNDERZEALOT.get(), UnderzealotEntity.createAttributes().build());
        event.put(WATCHER.get(), WatcherEntity.createAttributes().build());
        event.put(CORRODENT.get(), CorrodentEntity.createAttributes().build());
        event.put(VESPER.get(), VesperEntity.createAttributes().build());
        event.put(FORSAKEN.get(), ForsakenEntity.createAttributes().build());
        event.put(SWEETISH_FISH.get(), SweetishFishEntity.createAttributes().build());
        event.put(CANIAC.get(), CaniacEntity.createAttributes().build());
        event.put(GUMBEEPER.get(), GumbeeperEntity.createAttributes().build());
        event.put(CANDICORN.get(), CandicornEntity.createAttributes().build());
        event.put(GUM_WORM.get(), GumWormEntity.createAttributes().build());
        event.put(CARAMEL_CUBE.get(), CaramelCubeEntity.createAttributes().build());
        event.put(GUMMY_BEAR.get(), GummyBearEntity.createAttributes().build());
        event.put(LICOWITCH.get(), LicowitchEntity.createAttributes().build());
        event.put(GINGERBREAD_MAN.get(), GingerbreadManEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void spawnPlacements(RegisterSpawnPlacementsEvent event) {
        // Custom spawn placement types for acid and soda fluids are defined in ACSpawnPlacementTypes.
        // Magnetic Caves monsters use checkMagneticCaveSpawnRules to allow spawning in lit areas (Tesla Bulbs etc.)
        event.register(TELETOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TeletorEntity::checkMagneticCaveSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(MAGNETRON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MagnetronEntity::checkMagneticCaveSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(BOUNDROID.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BoundroidEntity::checkMagneticCaveSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(FERROUSLIME.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FerrouslimeEntity::checkMagneticCaveSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(NOTOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NotorEntity::checkNotorSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(SUBTERRANODON.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SubterranodonEntity::checkSubterranodonSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(VALLUMRAPTOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VallumraptorEntity::checkPrehistoricSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(GROTTOCERATOPS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GrottoceratopsEntity::checkPrehistoricSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(TRILOCARIS.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TrilocarisEntity::checkTrilocarisSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(TREMORSAURUS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TremorsaurusEntity::checkPrehistoricSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(RELICHEIRUS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RelicheirusEntity::checkPrehistoricSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(LUXTRUCTOSAURUS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LuxtructosaurusEntity::checkPrehistoricSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ATLATITAN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, AtlatitanEntity::checkPrehistoricPostBossSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(NUCLEEPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NucleeperEntity::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(RADGILL.get(), ACSpawnPlacementTypes.IN_ACID, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RadgillEntity::checkRadgillSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(BRAINIAC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BrainiacEntity::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(GAMMAROACH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GammaroachEntity::checkGammaroachSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(RAYCAT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, RaycatEntity::checkRaycatSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(LANTERNFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LanternfishEntity::checkLanternfishSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(SEA_PIG.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SeaPigEntity::checkSeaPigSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(HULLBREAKER.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, HullbreakerEntity::checkHullbreakerSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(GOSSAMER_WORM.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GossamerWormEntity::checkGossamerWormSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(TRIPODFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, TripodfishEntity::checkTripodfishSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(DEEP_ONE.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DeepOneBaseEntity::checkDeepOneSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(DEEP_ONE_KNIGHT.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DeepOneBaseEntity::checkDeepOneSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(DEEP_ONE_MAGE.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DeepOneBaseEntity::checkDeepOneSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(MINE_GUARDIAN.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MineGuardianEntity::checkMineGuardianSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(GLOOMOTH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GloomothEntity::checkGloomothSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(UNDERZEALOT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, UnderzealotEntity::checkUnderzealotSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(WATCHER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WatcherEntity::checkWatcherSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(CORRODENT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CorrodentEntity::checkCorrodentSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(VESPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, VesperEntity::checkVesperSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(FORSAKEN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ForsakenEntity::checkForsakenSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(SWEETISH_FISH.get(), ACSpawnPlacementTypes.IN_PURPLE_SODA, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SweetishFishEntity::checkSweetishFishSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(CANIAC.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CaniacEntity::checkCaniacSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(GUMBEEPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GumbeeperEntity::checkGumbeeperSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(CANDICORN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CandicornEntity::checkCandicornSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(GUM_WORM.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GumWormEntity::checkGumWormSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(CARAMEL_CUBE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CaramelCubeEntity::checkCaramelCubeSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(GUMMY_BEAR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GummyBearEntity::checkGummyBearSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(LICOWITCH.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LicowitchEntity::checkLicowitchSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(GINGERBREAD_MAN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, GingerbreadManEntity::checkGingerbreadManSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
    }
}
