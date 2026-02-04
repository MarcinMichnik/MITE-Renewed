package miterenewed.mixin;

import net.minecraft.core.component.DataComponents;
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
        // 1. Run the original check first (respects vanilla's rules)
        if (!slot.mayPickup(player)) return false;
        boolean isOutput = slot instanceof ResultSlot || slot.container instanceof ResultContainer;
        if (!isOutput) return true;

        ItemStack stack = slot.getItem();

        int req = getRequiredLevel(stack);
        if (player.experienceLevel < req) {
            player.playSound(SoundEvents.VILLAGER_NO, 1.0f, 1.0f);
            return false;
        }

        return true;
    }

    private int getRequiredLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        boolean isEnchantable = stack.has(DataComponents.ENCHANTABLE);
        if (isEnchantable) {
            if (stack.getDisplayName().getString().toLowerCase().contains("netherite")) return 35;
            if (stack.getDisplayName().getString().toLowerCase().contains("diamond")) return 20;
            if (stack.getDisplayName().getString().toLowerCase().contains("gold")) return 12;
            if (stack.getDisplayName().getString().toLowerCase().contains("iron")) return 10;
        }
        return 0;
    }
}