package miterenewed.mixin;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantResultSlot.class)
public abstract class MiteTradeCostMixin {

    @Shadow @Final private Player player;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void mite$subtractXpOnTrade(Player playerIn, ItemStack stack, CallbackInfo ci) {
        int cost = 50;
        if (playerIn.totalExperience >= cost) {
            playerIn.giveExperiencePoints(-cost);
            playerIn.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 0.7f);
        }
    }
}