package com.github.alexmodguy.alexscaves.server.misc;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.config.BiomeGenerationConfig;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.level.biome.ACBiomeRegistry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Fabric has no global loot modifiers, so the data/alexscaves/loot_modifiers entries (cave tablets and cabin maps found in
 * vanilla and modded loot tables) are applied by editing the matching loot tables as they load.
 */
public final class ACLootModifiers {

    private record Entry(String type, ResourceKey<Biome> biome, boolean replace, Set<ResourceLocation> tables) {
    }

    private static List<Entry> entries;

    private ACLootModifiers() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            for (Entry entry : getEntries()) {
                if (!entry.tables().contains(key.location())) {
                    continue;
                }
                if (entry.type().equals("cabin_map")) {
                    float chance = AlexsCaves.COMMON_CONFIG.cabinMapLootChance.get().floatValue();
                    tableBuilder.withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1.0F))
                        .when(LootItemRandomChanceCondition.randomChance(chance))
                        .add(LootItem.lootTableItem(Items.MAP).apply(CabinMapLootFunction.builder())));
                } else if (entry.type().equals("cave_tablet")) {
                    float chance = getTabletChance(entry.biome());
                    if (entry.replace()) {
                        tableBuilder.apply(ReplaceWithTabletLootFunction.builder(entry.biome(), chance));
                    } else {
                        CompoundTag tag = new CompoundTag();
                        tag.putString("CaveBiome", entry.biome().location().toString());
                        tableBuilder.withPool(LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(1.0F))
                            .when(LootItemRandomChanceCondition.randomChance(chance))
                            .add(LootItem.lootTableItem(ACItemRegistry.CAVE_TABLET.get())
                                .apply(SetComponentsFunction.setComponent(DataComponents.CUSTOM_DATA, CustomData.of(tag)))));
                    }
                }
            }
        });
    }

    public static ItemStack createTablet(ResourceKey<Biome> biome) {
        CompoundTag tag = new CompoundTag();
        tag.putString("CaveBiome", (biome == null ? ACBiomeRegistry.MAGNETIC_CAVES : biome).location().toString());
        ItemStack stack = new ItemStack(ACItemRegistry.CAVE_TABLET.get());
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    private static float getTabletChance(ResourceKey<Biome> biome) {
        if (biome == null || BiomeGenerationConfig.isBiomeDisabledCompletely(biome)) {
            return 0F;
        }
        if (biome.equals(ACBiomeRegistry.MAGNETIC_CAVES)) {
            return AlexsCaves.COMMON_CONFIG.magneticTabletLootChance.get().floatValue();
        }
        if (biome.equals(ACBiomeRegistry.PRIMORDIAL_CAVES)) {
            return AlexsCaves.COMMON_CONFIG.primordialTabletLootChance.get().floatValue();
        }
        if (biome.equals(ACBiomeRegistry.TOXIC_CAVES)) {
            return AlexsCaves.COMMON_CONFIG.toxicTabletLootChance.get().floatValue();
        }
        if (biome.equals(ACBiomeRegistry.ABYSSAL_CHASM)) {
            return AlexsCaves.COMMON_CONFIG.abyssalTabletLootChance.get().floatValue();
        }
        if (biome.equals(ACBiomeRegistry.FORLORN_HOLLOWS)) {
            return AlexsCaves.COMMON_CONFIG.forlornTabletLootChance.get().floatValue();
        }
        if (biome.equals(ACBiomeRegistry.CANDY_CAVITY)) {
            return AlexsCaves.COMMON_CONFIG.candyTabletLootChance.get().floatValue();
        }
        return 0F;
    }

    private static synchronized List<Entry> getEntries() {
        if (entries == null) {
            entries = new ArrayList<>();
            var container = FabricLoader.getInstance().getModContainer(AlexsCaves.MODID);
            if (container.isPresent()) {
                try {
                    Path global = container.get().findPath("data/c/loot_modifiers/global_loot_modifiers.json").orElse(null);
                    if (global != null) {
                        JsonArray list = JsonParser.parseString(Files.readString(global)).getAsJsonObject().getAsJsonArray("entries");
                        for (JsonElement element : list) {
                            ResourceLocation id = ResourceLocation.parse(element.getAsString());
                            Path file = container.get().findPath("data/" + id.getNamespace() + "/loot_modifiers/" + id.getPath() + ".json").orElse(null);
                            if (file != null) {
                                entries.add(parseEntry(JsonParser.parseString(Files.readString(file)).getAsJsonObject()));
                            }
                        }
                    }
                } catch (IOException | RuntimeException e) {
                    AlexsCaves.LOGGER.warn("Failed to read Alex's Caves loot modifiers", e);
                }
            }
        }
        return entries;
    }

    private static Entry parseEntry(JsonObject json) {
        String type = ResourceLocation.parse(json.get("type").getAsString()).getPath();
        ResourceKey<Biome> biome = json.has("biome") ? ResourceKey.create(Registries.BIOME, ResourceLocation.parse(json.get("biome").getAsString())) : null;
        boolean replace = json.has("replace") && json.get("replace").getAsBoolean();
        Set<ResourceLocation> tables = new HashSet<>();
        for (JsonElement condition : json.getAsJsonArray("conditions")) {
            tables.add(ResourceLocation.parse(condition.getAsJsonObject().get("loot_table_id").getAsString()));
        }
        return new Entry(type, biome, replace, tables);
    }
}
