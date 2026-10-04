package com.github.alexmodguy.alexscaves.server.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Fabric replacement for a "replace" CaveTabletLootModifier: with the configured chance the loot is swapped for a cave tablet.
 */
public class ReplaceWithTabletLootFunction extends LootItemConditionalFunction {

    public static final MapCodec<ReplaceWithTabletLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
        commonFields(instance).and(instance.group(
            ResourceKey.codec(Registries.BIOME).fieldOf("biome").forGetter(f -> f.biome),
            Codec.FLOAT.fieldOf("chance").forGetter(f -> f.chance)
        )).apply(instance, ReplaceWithTabletLootFunction::new)
    );

    private final ResourceKey<Biome> biome;
    private final float chance;

    public ReplaceWithTabletLootFunction(List<LootItemCondition> conditions, ResourceKey<Biome> biome, float chance) {
        super(conditions);
        this.biome = biome;
        this.chance = chance;
    }

    public static LootItemConditionalFunction.Builder<?> builder(ResourceKey<Biome> biome, float chance) {
        return simpleBuilder(conditions -> new ReplaceWithTabletLootFunction(conditions, biome, chance));
    }

    @Override
    protected ItemStack run(ItemStack stack, @NotNull LootContext context) {
        if (context.getRandom().nextFloat() < chance) {
            return ACLootModifiers.createTablet(biome);
        }
        return stack;
    }

    @Override
    public @NotNull LootItemFunctionType<ReplaceWithTabletLootFunction> getType() {
        return ACLootTableRegistry.REPLACE_WITH_TABLET_LOOT_FUNCTION.get();
    }
}
