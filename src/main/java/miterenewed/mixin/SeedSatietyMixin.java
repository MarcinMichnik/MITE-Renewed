package miterenewed.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(ItemStack.class)
public abstract class SeedSatietyMixin {

    private static final Set<Item> EDIBLE_SEEDS = Set.of(
            Items.WHEAT_SEEDS,
            Items.PUMPKIN_SEEDS,
            Items.MELON_SEEDS,
            Items.BEETROOT_SEEDS
    );

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void applySeedSatiety(Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (!level.isClientSide() && entity instanceof Player player) {
            ItemStack stack = (ItemStack) (Object) this;
            if (EDIBLE_SEEDS.contains(stack.getItem())) {
                FoodProperties foodComponent = stack.get(DataComponents.FOOD);
                if (foodComponent != null) {
                    // Manually inject the saturation value.
                    float currentSat = player.getFoodData().getSaturationLevel();
                    player.getFoodData().setSaturation(currentSat + 0.5f);
                }
            }
        }
    }
}