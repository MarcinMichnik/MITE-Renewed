package miterenewed.mixin.crafting;

import miterenewed.ModConstants;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Smelting or blasting anything into a copper or iron ingot gives more XP; the furnace reads experience() when popping orbs. */
@Mixin(AbstractCookingRecipe.class)
public abstract class IngotSmeltingXpMixin extends SingleItemRecipe {
    private IngotSmeltingXpMixin(Recipe.CommonInfo commonInfo, Ingredient input, ItemStackTemplate result) {
        super(commonInfo, input, result);
    }

    @Inject(method = "experience", at = @At("RETURN"), cancellable = true)
    private void mite$moreIngotXp(CallbackInfoReturnable<Float> cir) {
        Item item = result().item().value();
        if (item == Items.COPPER_INGOT) {
            cir.setReturnValue(cir.getReturnValue() * ModConstants.COPPER_SMELTING_XP_MULTIPLIER);
        } else if (item == Items.IRON_INGOT) {
            cir.setReturnValue(cir.getReturnValue() * ModConstants.IRON_SMELTING_XP_MULTIPLIER);
        }
    }
}
