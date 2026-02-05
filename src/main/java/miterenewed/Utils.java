package miterenewed;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class Utils {
    public static int getMaxFoodLevel(ServerPlayer player) {
        int bonus = player.experienceLevel / ModConstants.LEVELS_PER_UPGRADE;
        return (ModConstants.BASE_HUNGER + bonus) * 2;
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

    public static void addToTooltip(List<Component> tooltip, int req, boolean reqMet, boolean isCraftingMenu) {
        ChatFormatting color = reqMet ? ChatFormatting.GREEN : ChatFormatting.RED;
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("⚒ FORGE KNOWLEDGE").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Requires: Level " + req).withStyle(color));
        int expCost = req * ModConstants.CRAFTING_EXP_COST_MODIFIER;
        if (isCraftingMenu) {
            tooltip.add(Component.literal("Crafting Cost: " + expCost + " Experience")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

}
