package miterenewed.mixin;

import miterenewed.Utils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerMenu.class)
public abstract class ResultContainerPickupLimiterMixin {
    @Redirect(
            method = "*", // Redirects the call in every method within this class
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/Slot;mayPickup(Lnet/minecraft/world/entity/player/Player;)Z")
    )
    private boolean redirectSlotPickupCall(Slot slot, Player player) {
        if (!slot.mayPickup(player)) return false;
        boolean isOutput = slot instanceof ResultSlot || slot.container instanceof ResultContainer;
        if (!isOutput) return true;

        ItemStack stack = slot.getItem();

        int req = Utils.getRequiredLevel(stack);
        if (player.experienceLevel < req) {
            player.playSound(SoundEvents.VILLAGER_NO, 1.0f, 1.0f);
            return false;
        }

        return true;
    }
}