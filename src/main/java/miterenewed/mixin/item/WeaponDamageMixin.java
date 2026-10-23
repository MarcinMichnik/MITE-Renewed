package miterenewed.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import miterenewed.ModConstants;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Gives every sword and axe material its own attack damage. Vanilla swords share one base value, so
 * stone and copper hit equally hard, and most axes sit at 9. The material's damage bonus is shared
 * with pickaxes, shovels and spears, so it's left alone and the per-item base is adjusted instead.
 */
@Mixin(Item.Properties.class)
public abstract class WeaponDamageMixin {
    @WrapOperation(method = "sword", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/ToolMaterial;applySwordProperties(Lnet/minecraft/world/item/Item$Properties;FF)Lnet/minecraft/world/item/Item$Properties;"))
    private Item.Properties mite$swordDamage(ToolMaterial material, Item.Properties properties, float attackDamage,
                                             float attackSpeed, Operation<Item.Properties> original) {
        float total = mite$swordDamage(material);
        if (total > 0) {
            attackDamage = mite$toBaseDamage(total, material);
        }
        return original.call(material, properties, attackDamage, attackSpeed);
    }

    @WrapOperation(method = "axe", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/item/Item$Properties;tool(Lnet/minecraft/world/item/ToolMaterial;Lnet/minecraft/tags/TagKey;FFF)Lnet/minecraft/world/item/Item$Properties;"))
    private Item.Properties mite$axeDamage(Item.Properties properties, ToolMaterial material, TagKey<Block> mineable,
                                           float attackDamage, float attackSpeed, float disableBlockingSeconds,
                                           Operation<Item.Properties> original) {
        float total = mite$axeDamage(material);
        if (total > 0) {
            attackDamage = mite$toBaseDamage(total, material);
        }
        return original.call(properties, material, mineable, attackDamage, attackSpeed, disableBlockingSeconds);
    }

    // Tooltip damage = 1 (player base) + item base + material bonus
    @Unique
    private static float mite$toBaseDamage(float total, ToolMaterial material) {
        return total - 1 - material.attackDamageBonus();
    }

    // 0 = unknown (modded) material, keep its vanilla value
    @Unique
    private static float mite$swordDamage(ToolMaterial material) {
        if (material == ToolMaterial.WOOD) return ModConstants.WOODEN_SWORD_DAMAGE;
        if (material == ToolMaterial.STONE) return ModConstants.STONE_SWORD_DAMAGE;
        if (material == ToolMaterial.COPPER) return ModConstants.COPPER_SWORD_DAMAGE;
        if (material == ToolMaterial.GOLD) return ModConstants.GOLDEN_SWORD_DAMAGE;
        if (material == ToolMaterial.IRON) return ModConstants.IRON_SWORD_DAMAGE;
        if (material == ToolMaterial.DIAMOND) return ModConstants.DIAMOND_SWORD_DAMAGE;
        if (material == ToolMaterial.NETHERITE) return ModConstants.NETHERITE_SWORD_DAMAGE;
        return 0;
    }

    @Unique
    private static float mite$axeDamage(ToolMaterial material) {
        if (material == ToolMaterial.WOOD) return ModConstants.WOODEN_AXE_DAMAGE;
        if (material == ToolMaterial.STONE) return ModConstants.STONE_AXE_DAMAGE;
        if (material == ToolMaterial.COPPER) return ModConstants.COPPER_AXE_DAMAGE;
        if (material == ToolMaterial.GOLD) return ModConstants.GOLDEN_AXE_DAMAGE;
        if (material == ToolMaterial.IRON) return ModConstants.IRON_AXE_DAMAGE;
        if (material == ToolMaterial.DIAMOND) return ModConstants.DIAMOND_AXE_DAMAGE;
        if (material == ToolMaterial.NETHERITE) return ModConstants.NETHERITE_AXE_DAMAGE;
        return 0;
    }
}
