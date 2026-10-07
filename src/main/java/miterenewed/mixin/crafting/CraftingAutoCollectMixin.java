package miterenewed.mixin.crafting;

import miterenewed.CraftingTimer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The server calls broadcastChanges on the open menu every tick; use it to hand over finished
 * crafts. Running at HEAD means the moved item is synced to the client in the same tick.
 */
@Mixin(AbstractContainerMenu.class)
public abstract class CraftingAutoCollectMixin {
    @Inject(method = "broadcastChanges", at = @At("HEAD"))
    private void mite$collectFinishedCraft(CallbackInfo ci) {
        if ((Object) this instanceof CraftingTimer timer) {
            timer.mite$tickCraft();
        }
    }
}
