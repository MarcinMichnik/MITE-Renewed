package miterenewed.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Set;

@Mixin(Items.class)
public class ModifyVanillaItemsMixin {
    @ModifyVariable(
            method = "registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static Item.Properties modifyVanillaItems(Item.Properties properties, ResourceKey<Item> key) {
        Set<String> SEED_IDS = Set.of(
                "wheat_seeds",
                "pumpkin_seeds",
                "melon_seeds",
                "beetroot_seeds"
        );

        // Check if the item being registered is one of our target seeds
        String fullName = key.identifier().getPath();
        if (SEED_IDS.stream().anyMatch(fullName::contains)) {
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