package miterenewed.mixin;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantMenu.class)
public abstract class MerchantShiftClickGuardMixin {

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void mite$blockBulkShiftTrade(Player player, int slotIndex, CallbackInfoReturnable<ItemStack> cir) {
        if (slotIndex == 2) {
            MerchantMenu menu = (MerchantMenu) (Object) this;
            Slot slot = menu.getSlot(slotIndex);

            if (slot.hasItem()) {
                ItemStack stackInSlot = slot.getItem();
                int expCostPerTrade = 50;

                int tradeCount = stackInSlot.getCount();
                int totalRequiredXp = expCostPerTrade * tradeCount;

                if (player.totalExperience < totalRequiredXp) {
                    player.playSound(SoundEvents.VILLAGER_NO, 0.5f, 1.0f);
                    cir.setReturnValue(ItemStack.EMPTY);
                }
            }
        }
    }
}