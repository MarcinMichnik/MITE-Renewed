package miterenewed.mixin.crafting;

import miterenewed.CraftingTimer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Taking a result starts the next craft from zero, even when the same item is crafted again. */
@Mixin(ResultSlot.class)
public abstract class CraftingRestartMixin {
    @Inject(method = "onTake", at = @At("TAIL"))
    private void mite$restartCraft(Player player, ItemStack stack, CallbackInfo ci) {
        if (player.containerMenu instanceof CraftingTimer timer) {
            timer.mite$restartCraft();
        }
    }
}
