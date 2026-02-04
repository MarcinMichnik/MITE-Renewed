package miterenewed.mixin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public class ItemExperienceCostMixin {

    @Inject(method = "onTake", at = @At("HEAD"))
    private void consumeExperienceOnCraft(Player playerIn, ItemStack stack, CallbackInfo ci) {
        boolean isCrafting = playerIn.containerMenu instanceof CraftingMenu ||
                playerIn.containerMenu instanceof InventoryMenu;
        if (!isCrafting) return;
        int req = getRequiredLevel(stack);
        if (req > 0) {
            if (playerIn.experienceLevel >= req) {
                playerIn.giveExperiencePoints(-req * 4);
                playerIn.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);
            }
        }
    }

    private int getRequiredLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        boolean isEnchantable = stack.has(DataComponents.ENCHANTABLE);
        if (isEnchantable) {
            if (stack.getDisplayName().getString().toLowerCase().contains("netherite")) return 35;
            if (stack.getDisplayName().getString().toLowerCase().contains("diamond")) return 20;
            if (stack.getDisplayName().getString().toLowerCase().contains("gold")) return 12;
            if (stack.getDisplayName().getString().toLowerCase().contains("iron")) return 10;
            if (stack.getDisplayName().getString().toLowerCase().contains("copper")) return 5;
        }
        return 0;
    }
}
