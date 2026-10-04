package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ACLootTableRegistry {

    @SuppressWarnings("unchecked")
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_DEF_REG = DeferredRegister.create((ResourceKey) NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, AlexsCaves.MODID);
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTION_DEF_REG = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, AlexsCaves.MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<? extends IGlobalLootModifier>> CAVE_TABLET_LOOT_MODIFIER = GLOBAL_LOOT_MODIFIER_DEF_REG.register("cave_tablet", () -> CaveTabletLootModifier.CODEC);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<? extends IGlobalLootModifier>> CABIN_MAP_LOOT_MODIFIER = GLOBAL_LOOT_MODIFIER_DEF_REG.register("cabin_map", () -> CabinMapLootModifier.CODEC);
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<CabinMapLootFunction>> CABIN_MAP_LOOT_FUNCTION = LOOT_FUNCTION_DEF_REG.register("cabin_map", () -> new LootItemFunctionType<>(CabinMapLootFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<ReplaceWithTabletLootFunction>> REPLACE_WITH_TABLET_LOOT_FUNCTION = LOOT_FUNCTION_DEF_REG.register("replace_with_tablet", () -> new LootItemFunctionType<>(ReplaceWithTabletLootFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<GummyColorLootFunction>> GUMMY_COLORS_LOOT_FUNCTION = LOOT_FUNCTION_DEF_REG.register("gummy_colors", () -> new LootItemFunctionType<>(GummyColorLootFunction.CODEC));

    public static final ResourceKey<LootTable> ABYSSAL_RUINS_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/abyssal_ruins"));
    public static final ResourceKey<LootTable> WITCH_HUT_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/witch_hut"));
    public static final ResourceKey<LootTable> LICOWITCH_TOWER_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/licowitch_tower"));
    public static final ResourceKey<LootTable> SECRET_LICOWITCH_TOWER_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/licowitch_tower_secret"));
    public static final ResourceKey<LootTable> GINGERBREAD_TOWN_CHEST = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(AlexsCaves.MODID, "chests/gingerbread_town"));

}
