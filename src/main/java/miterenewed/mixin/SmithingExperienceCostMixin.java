package miterenewed.mixin;


import miterenewed.ModConstants;
import miterenewed.Utils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingMenu.class)
public class SmithingExperienceCostMixin {
    @Inject(method = "onTake", at = @At("HEAD"))
    private void mite$consumeXpOnSmithingTake(Player player, ItemStack stack, CallbackInfo ci) {
        int req = Utils.getRequiredLevel(stack);
        if (req > 0 && player.experienceLevel >= req) {
            player.giveExperiencePoints(-req * ModConstants.CRAFTING_EXP_COST_MODIFIER);
            player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);
        }
    }

}
