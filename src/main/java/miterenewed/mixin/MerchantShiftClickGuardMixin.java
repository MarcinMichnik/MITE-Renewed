package miterenewed.mixin;

import miterenewed.ModConstants;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
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

            MerchantContainer container = ((MerchantMenuAccessor) menu).getTradeContainer();
            MerchantOffer offer = container.getActiveOffer();

            if (offer != null && !offer.getResult().isEmpty()) {
                Slot slot = menu.getSlot(slotIndex);

                if (slot.hasItem()) {
                    ItemStack stackInSlot = slot.getItem();

                    int amountPerTrade = offer.getResult().getCount();
                    if (amountPerTrade <= 0) amountPerTrade = 1;

                    int actualTradeUnits = stackInSlot.getCount() / amountPerTrade;

                    int expCostPerUnit = offer.getXp() * ModConstants.TRADE_COST_MODIFIER;
                    int totalRequiredXp = expCostPerUnit * actualTradeUnits;

                    if (player.totalExperience < totalRequiredXp) {
                        if (player.level().isClientSide()) {
                            player.playSound(SoundEvents.VILLAGER_NO, 0.5f, 1.0f);
                        }
                        cir.setReturnValue(ItemStack.EMPTY);
                    }
                }
            }
        }
    }
}