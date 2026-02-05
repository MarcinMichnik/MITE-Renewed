package miterenewed.mixin;

import miterenewed.ModConstants;
import miterenewed.Utils;
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
        int req = Utils.getRequiredLevel(stack);
        if (req > 0 && playerIn.experienceLevel >= req) {
            playerIn.giveExperiencePoints(-req * ModConstants.CRAFTING_EXP_COST_MODIFIER);
            playerIn.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);
        }
    }

}
