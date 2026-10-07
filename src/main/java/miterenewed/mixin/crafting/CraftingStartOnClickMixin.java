package miterenewed.mixin.crafting;

import miterenewed.CraftingTimer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.ContainerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Any click on the crafting result (click, shift-click, number key, Q) starts crafting it.
 * Runs before the click is handled, so the first click only starts the craft; once the arrow
 * is full, the next click takes the item.
 */
@Mixin(AbstractContainerMenu.class)
public abstract class CraftingStartOnClickMixin {
    @Inject(method = "clicked", at = @At("HEAD"))
    private void mite$startCraftOnClick(int slotId, int button, ContainerInput input, Player player, CallbackInfo ci) {
        if ((Object) this instanceof AbstractCraftingMenu menu
                && menu instanceof CraftingTimer timer
                && slotId >= 0 && slotId < menu.slots.size()
                && menu.getSlot(slotId) == menu.getResultSlot()) {
            timer.mite$startCraft();
        }
    }
}
