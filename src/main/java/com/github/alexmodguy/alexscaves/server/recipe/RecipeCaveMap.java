package com.github.alexmodguy.alexscaves.server.recipe;

import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.item.CaveInfoItem;
import com.github.alexmodguy.alexscaves.server.item.CaveMapItem;
import com.github.alexmodguy.alexscaves.citadel.recipe.SpecialRecipeInGuideBook;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class RecipeCaveMap extends CustomRecipe implements SpecialRecipeInGuideBook {
    
    private static final NonNullList<Ingredient> DISPLAY_INGREDIENTS = NonNullList.of(Ingredient.EMPTY, 
        Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), 
        Ingredient.of(Items.PAPER), Ingredient.of(ACItemRegistry.CAVE_CODEX.get()), Ingredient.of(Items.PAPER), 
        Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER));
    
    public RecipeCaveMap(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) {
            return false;
        }
        // Check that we have 8 papers and 1 cave codex in the center
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                ItemStack stack = input.getItem(x, y);
                if (x == 1 && y == 1) {
                    // Center must be cave codex
                    if (!stack.is(ACItemRegistry.CAVE_CODEX.get())) {
                        return false;
                    }
                } else {
                    // Others must be paper
                    if (!stack.is(Items.PAPER)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack scroll = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); ++i) {
            if (!input.getItem(i).isEmpty() && input.getItem(i).is(ACItemRegistry.CAVE_CODEX.get())) {
                if (scroll.isEmpty()) {
                    scroll = input.getItem(i);
                }
            }
        }
        ResourceKey<Biome> key = CaveInfoItem.getCaveBiome(scroll);
        if (key != null) {
            return CaveMapItem.createMap(key);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ACRecipeRegistry.CAVE_MAP.get();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(ACItemRegistry.CAVE_MAP.get());
    }

    @Override
    public NonNullList<Ingredient> getDisplayIngredients() {
        return DISPLAY_INGREDIENTS;
    }

    @Override
    public ItemStack getDisplayResultFor(NonNullList<ItemStack> nonNullList) {
        ItemStack scroll = ItemStack.EMPTY;
        for (int i = 0; i < nonNullList.size(); ++i) {
            if (!nonNullList.get(i).isEmpty() && nonNullList.get(i).is(ACItemRegistry.CAVE_CODEX.get())) {
                if (scroll.isEmpty()) {
                    scroll = nonNullList.get(i);
                }
            }
        }
        ResourceKey<Biome> key = CaveInfoItem.getCaveBiome(scroll);
        if (key != null) {
            return CaveMapItem.createMap(key);
        }
        return ItemStack.EMPTY;
    }
}

