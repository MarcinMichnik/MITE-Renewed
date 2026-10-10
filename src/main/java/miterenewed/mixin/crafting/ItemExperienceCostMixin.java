package miterenewed.mixin.crafting;

import miterenewed.ModConstants;
import miterenewed.Utils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public class ItemExperienceCostMixin {
    @Shadow @Final private Player player;

    // Charged here rather than in onTake: shift-click (and auto-collect) passes onTake the leftover
    // stack after moving the item into the inventory, which is empty. checkTakeAchievements always
    // gets the crafted stack once per craft (a second call with the empty leftover costs nothing).
    @Inject(method = "checkTakeAchievements", at = @At("HEAD"))
    private void consumeExperienceOnCraft(ItemStack stack, CallbackInfo ci) {
        boolean isCrafting = player.containerMenu instanceof CraftingMenu ||
                player.containerMenu instanceof InventoryMenu;
        if (!isCrafting) return;
        int req = Utils.getRequiredLevel(stack);
        if (req > 0 && player.experienceLevel >= req) {
            player.giveExperiencePoints(-Utils.getCraftingExpCost(stack));
            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);
        }
    }

}
