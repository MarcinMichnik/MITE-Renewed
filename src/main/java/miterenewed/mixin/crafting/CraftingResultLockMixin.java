package miterenewed.mixin.crafting;

import miterenewed.CraftingTimer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The crafting result can't be taken until it has finished crafting. Every way of taking it
 * (click, shift-click, number-key swap, Q-drop) goes through mayPickup.
 */
@Mixin(Slot.class)
public abstract class CraftingResultLockMixin {
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void mite$waitForCraft(Player player, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ResultSlot
                && player.containerMenu instanceof AbstractCraftingMenu menu
                && menu.getResultSlot() == (Object) this
                && menu instanceof CraftingTimer timer
                && !timer.mite$isCraftReady()) {
            cir.setReturnValue(false);
        }
    }
}
