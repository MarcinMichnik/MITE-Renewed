package miterenewed;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Hovering food shows what eating it gives: nutrition (food points) and satiety (saturation points).
 * Values that are zero are left out, so seeds show only their satiety.
 */
public final class FoodTooltip {
    private FoodTooltip() {}

    public static void register() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            FoodProperties food = stack.get(DataComponents.FOOD);
            if (food == null) return;

            List<Component> foodLines = new ArrayList<>();
            if (food.nutrition() > 0) {
                foodLines.add(Component.literal("Nutrition: +" + food.nutrition()).withStyle(ChatFormatting.GOLD));
            }
            if (food.saturation() > 0) {
                foodLines.add(Component.literal("Satiety: +" + format(food.saturation())).withStyle(ChatFormatting.YELLOW));
            }
            // Right below the item name
            lines.addAll(Math.min(1, lines.size()), foodLines);
        });
    }

    /** 0.5 -> "0.5", 2.0 -> "2", 1.2000000476 -> "1.2" */
    private static String format(float value) {
        float rounded = Math.round(value * 10) / 10f;
        return rounded == (int) rounded ? Integer.toString((int) rounded) : Float.toString(rounded);
    }
}
