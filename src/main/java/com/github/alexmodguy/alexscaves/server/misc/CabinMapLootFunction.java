package com.github.alexmodguy.alexscaves.server.misc;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Fabric replacement for NeoForge's CabinMapLootModifier: turns a generated stack into an underground cabin explorer map.
 */
public class CabinMapLootFunction extends LootItemConditionalFunction {

    public static final MapCodec<CabinMapLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
        commonFields(instance).apply(instance, CabinMapLootFunction::new)
    );

    protected CabinMapLootFunction(List<LootItemCondition> conditions) {
        super(conditions);
    }

    public static LootItemConditionalFunction.Builder<?> builder() {
        return simpleBuilder(CabinMapLootFunction::new);
    }

    @Override
    protected ItemStack run(ItemStack stack, @NotNull LootContext context) {
        if (!context.hasParam(LootContextParams.ORIGIN)) {
            return ItemStack.EMPTY;
        }
        ServerLevel serverlevel = context.getLevel();
        BlockPos chestPos = BlockPos.containing(context.getParam(LootContextParams.ORIGIN));
        BlockPos blockpos = serverlevel.findNearestMapStructure(ACTagRegistry.ON_UNDERGROUND_CABIN_MAPS, chestPos, 100, true);
        if (blockpos == null) {
            return ItemStack.EMPTY;
        }
        ItemStack itemstack = MapItem.create(serverlevel, blockpos.getX(), blockpos.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(serverlevel, itemstack);
        MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", ACVanillaMapUtil.getUndergroundCabinDecoration());
        itemstack.set(DataComponents.CUSTOM_NAME, Component.translatable("item.alexscaves.underground_cabin_explorer_map"));
        return itemstack;
    }

    @Override
    public @NotNull LootItemFunctionType<CabinMapLootFunction> getType() {
        return ACLootTableRegistry.CABIN_MAP_LOOT_FUNCTION.get();
    }
}
