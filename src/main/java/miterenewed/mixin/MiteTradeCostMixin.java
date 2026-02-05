package miterenewed.mixin;

import miterenewed.ModConstants;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantResultSlot.class)
public abstract class MiteTradeCostMixin {

    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
    private void mite$subtractXpOnTrade(Player playerIn, ItemStack stack, CallbackInfo ci) {
        MerchantResultSlot merchantResultSlot = (MerchantResultSlot)(Object)this;
        MerchantContainer cont = (MerchantContainer)merchantResultSlot.container;
        MerchantOffer offer = cont.getActiveOffer();
        int cost = offer.getXp() * ModConstants.TRADE_COST_MODIFIER;
        if (playerIn.totalExperience >= cost) {
            playerIn.giveExperiencePoints(-cost);
            playerIn.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 0.7f);
        }
    }
}