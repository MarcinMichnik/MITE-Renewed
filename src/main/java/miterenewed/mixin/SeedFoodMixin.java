package miterenewed.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Items.class)
public class SeedFoodMixin {

    @ModifyVariable(
            method = "registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static Item.Properties injectSeedFood(Item.Properties properties, ResourceKey<Item> key) {
        // Check if the item being registered is one of our target seeds
        String fullName = key.identifier().getPath();
        if (fullName.contains("wheat_seeds") ||
                fullName.contains("pumpkin_seeds") ||
                fullName.contains("melon_seeds") ||
                fullName.contains("beetroot_seeds")) {

            FoodProperties seedFood = new FoodProperties.Builder()
                    .nutrition(0)
                    .saturationModifier(0f) // Will be overwritten in SeedSatietyMixin
                    .build();

            // Return a new properties object or modify the existing one
            return properties.food(seedFood);
        }

        return properties;
    }
}