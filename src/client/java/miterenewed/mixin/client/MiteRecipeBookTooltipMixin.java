package miterenewed.mixin.client;

import miterenewed.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeButton.class)
public class MiteRecipeBookTooltipMixin {
    @Inject(method = "getTooltipText", at = @At("RETURN"))
    private void addRecipeBookRequirement(CallbackInfoReturnable<List<Component>> cir) {
        RecipeButton button = (RecipeButton) (Object) this;
        ItemStack entry = button.getDisplayStack();

        int req = Utils.getRequiredLevel(entry);
        if (req > 0) {
            List<Component> tooltip = cir.getReturnValue();
            Player player = Minecraft.getInstance().player;

            boolean met = player != null && player.experienceLevel >= req;
            Utils.addToTooltip(tooltip, req, met, true);
        }
    }

}
