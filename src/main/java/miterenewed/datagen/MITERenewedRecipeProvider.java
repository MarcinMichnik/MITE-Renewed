package miterenewed.datagen;

import miterenewed.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class MITERenewedRecipeProvider extends FabricRecipeProvider {
    public MITERenewedRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                shaped(RecipeCategory.COMBAT, ModItems.FLINT_HATCHET)
                        .pattern("SF")
                        .pattern("S ")
                        .define('F', Items.FLINT)
                        .define('S', Items.STICK)
                        .unlockedBy(getHasName(Items.FLINT), has(Items.FLINT))
                        .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                        .save(output);
            }
        };
    }

    @Override
    public String getName() {
        return "MITERenewedRecipeProvider";
    }
}
