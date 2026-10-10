package miterenewed;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class Utils {
    /** Maximum food points (2 per hunger bar): BASE_HUNGER bars plus one per LEVELS_PER_UPGRADE levels, up to 10 bars. */
    public static int getMaxFoodLevel(Player player) {
        int bonus = player.experienceLevel / ModConstants.LEVELS_PER_UPGRADE;
        return Math.min(ModConstants.BASE_HUNGER + bonus, 10) * 2;
    }

    public static int getRequiredLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        boolean isEnchantable = stack.has(DataComponents.ENCHANTABLE);
        if (isEnchantable) {
            String itemName = stack.getDisplayName().getString().toLowerCase();
            if (itemName.contains("netherite")) return 35;
            if (itemName.contains("diamond")) return 20;
            if (itemName.contains("gold")) return 12;
            if (itemName.contains("iron")) return 10;
            if (itemName.contains("copper")) return 5;
        }
        return 0;
    }

    /** Experience points taken when crafting or smithing an item that has a level requirement. */
    public static int getCraftingExpCost(ItemStack stack) {
        int req = getRequiredLevel(stack);
        if (req > 0 && stack.getDisplayName().getString().toLowerCase().contains("copper")) {
            return ModConstants.COPPER_CRAFTING_EXP_COST;
        }
        return req * ModConstants.CRAFTING_EXP_COST_MODIFIER;
    }

    /**
     * Crafting time in ticks: a base time plus a per-ingredient time. Gear (tools, weapons, armor)
     * takes longer per ingredient the stronger its material is.
     */
    public static int getCraftingTicks(ItemStack result, int ingredients) {
        if (result.isEmpty()) return 0;
        int perIngredient = ModConstants.CRAFTING_TICKS_PER_INGREDIENT;
        if (result.has(DataComponents.ENCHANTABLE) || result.isDamageableItem()) {
            String id = BuiltInRegistries.ITEM.getKey(result.getItem()).getPath();
            if (id.contains("netherite")) perIngredient = 30;
            else if (id.contains("diamond")) perIngredient = 25;
            else if (id.contains("iron")) perIngredient = 15;
            else if (id.contains("golden")) perIngredient = 12;
            else if (id.contains("copper")) perIngredient = 10;
            else if (id.contains("stone") || id.contains("chainmail")) perIngredient = 8;
            else if (id.contains("wooden") || id.contains("leather")) perIngredient = 5;
            else perIngredient = 6; // bows, shields, fishing rods...
        }
        return ModConstants.CRAFTING_BASE_TICKS + perIngredient * ingredients;
    }

    public static void addToTooltip(List<Component> tooltip, int req, int expCost, boolean reqMet, boolean isCraftingMenu) {
        ChatFormatting color = reqMet ? ChatFormatting.GREEN : ChatFormatting.RED;
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("⚒ FORGE KNOWLEDGE").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Requires: Level " + req).withStyle(color));
        if (isCraftingMenu) {
            tooltip.add(Component.literal("Crafting Cost: " + expCost + " Experience")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    /**
     * Leaves are transparent to skeletons (skeleton, stray, bogged...) and the arrows they shoot:
     * they can spot targets through foliage and their arrows fly through it.
     */
    public static boolean seesThroughLeaves(Entity entity, BlockState state) {
        boolean skeletonOrItsArrow = entity instanceof AbstractSkeleton
                || entity instanceof AbstractArrow arrow && arrow.getOwner() instanceof AbstractSkeleton;
        return skeletonOrItsArrow && state.is(BlockTags.LEAVES);
    }

}
