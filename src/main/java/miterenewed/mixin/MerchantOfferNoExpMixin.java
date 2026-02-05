package miterenewed.mixin;

import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MerchantOffer.class)
public abstract class MerchantOfferNoExpMixin {
    @ModifyVariable(
            method = "<init>(Lnet/minecraft/world/item/trading/ItemCost;Ljava/util/Optional;Lnet/minecraft/world/item/ItemStack;IIZIIFI)V",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private static boolean mite$forceFalseRewardExp(boolean original) {
        return false;
    }
}