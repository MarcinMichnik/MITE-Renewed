package miterenewed.mixin.crafting;

import miterenewed.CraftingTimer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Shift-clicking the result makes vanilla craft repeatedly until the ingredients run out.
 * Stop after each finished craft so every item has to wait its own crafting time.
 */
@Mixin({CraftingMenu.class, InventoryMenu.class})
public abstract class CraftingQuickMoveMixin {
    @Unique private static final int RESULT_SLOT = 0;

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void mite$oneCraftAtATime(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        if (slotIndex == RESULT_SLOT && (Object) this instanceof CraftingTimer timer && !timer.mite$isCraftReady()) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
