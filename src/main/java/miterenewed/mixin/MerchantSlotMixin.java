package miterenewed.mixin;

import miterenewed.ModConstants;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class MerchantSlotMixin {
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void mite$playDenialSound(Player player, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = (Slot)(Object)this;
        boolean isMerchantSlot = slot instanceof MerchantResultSlot;
        if (!isMerchantSlot) return;
        MerchantResultSlot merchantResultSlot = (MerchantResultSlot)slot;
        MerchantContainer merchantContainer = (MerchantContainer)merchantResultSlot.container;
        MerchantOffer offer = merchantContainer.getActiveOffer();
        int expCost = offer.getXp() * ModConstants.TRADE_COST_MODIFIER;
        if (player.totalExperience < expCost) {
            player.playSound(SoundEvents.VILLAGER_NO, 0.5f, 1.0f);
            cir.setReturnValue(false);
        }
    }
}
