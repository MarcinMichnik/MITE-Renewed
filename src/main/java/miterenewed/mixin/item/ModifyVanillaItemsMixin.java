package miterenewed.mixin.item;

import miterenewed.MITERenewed;
import miterenewed.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Set;

@Mixin(Items.class)
public class ModifyVanillaItemsMixin {
    @ModifyVariable(
            method = "registerItem(Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Function;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static Item.Properties modifyVanillaItems(Item.Properties properties, ResourceKey<Item> key) {
        Set<String> SATURATION_ONLY_FOOD_IDS = Set.of(
                "wheat_seeds",
                "pumpkin_seeds",
                "melon_seeds",
                "beetroot_seeds",
                "sugar"
        );

        // Exact match, so "sugar" doesn't also catch "sugar_cane"
        if (SATURATION_ONLY_FOOD_IDS.contains(key.identifier().getPath())) {
            // No nutrition (hunger bars unchanged), a little saturation, edible even when full
            FoodProperties seedFood = new FoodProperties(0, ModConstants.SEED_SATURATION, true);

            // Return a new properties object or modify the existing one
            return properties.food(seedFood);
        }

        // Vanilla saplings share the dry plants burn time (100 ticks) with sticks; give them their own so it takes 6 to smelt one item (200 ticks)
        String path = key.identifier().getPath();
        if (path.endsWith("_sapling") || path.equals("mangrove_propagule")) {
            // Built here rather than as a static field: this runs during Items' static init, before mixin-added fields are set
            // Defined in data/mite-renewed/context_int_provider/cooking/time_saplings.json
            ResourceKey<ContextIntProvider> saplingCookingTime = ResourceKey.create(
                    Registries.CONTEXT_INT_PROVIDER, Identifier.fromNamespaceAndPath(MITERenewed.MOD_ID, "cooking/time_saplings"));
            return properties.cookingFuel(saplingCookingTime);
        }

        return properties;
    }

    // Every vanilla sword passes 3 here (the material adds the rest), so only touch the wooden sword's registration
    @ModifyArg(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;WOODEN_SWORD:Lnet/minecraft/resources/ResourceKey;"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;WOODEN_SWORD:Lnet/minecraft/world/item/Item;")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;sword(Lnet/minecraft/world/item/ToolMaterial;FF)Lnet/minecraft/world/item/Item$Properties;"),
            index = 1
    )
    private static float miteWoodenSwordDamage(float attackDamage) {
        // The sword's value is a bonus on top of the player's base 1 damage
        return ModConstants.WOODEN_SWORD_ATTACK_DAMAGE - 1.0F;
    }

    @ModifyArg(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/references/ItemIds;STONE_SWORD:Lnet/minecraft/resources/ResourceKey;"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;STONE_SWORD:Lnet/minecraft/world/item/Item;")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item$Properties;sword(Lnet/minecraft/world/item/ToolMaterial;FF)Lnet/minecraft/world/item/Item$Properties;"),
            index = 1
    )
    private static float miteStoneSwordDamage(float attackDamage) {
        // On top of the player's base 1 damage and the stone material's +1 bonus
        return ModConstants.STONE_SWORD_ATTACK_DAMAGE - 2.0F;
    }
}