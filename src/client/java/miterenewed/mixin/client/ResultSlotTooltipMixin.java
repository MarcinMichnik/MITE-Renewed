package miterenewed.mixin.client;

import miterenewed.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class ResultSlotTooltipMixin {
    @Shadow @Nullable protected Slot hoveredSlot;

    @Inject(method = "getTooltipFromContainerItem", at = @At("RETURN"))
    private void addResultOnlyTooltip(ItemStack stack, CallbackInfoReturnable<List<Component>> cir) {
        if (this.hoveredSlot == null) return;

        boolean isOutput = this.hoveredSlot instanceof ResultSlot ||
                this.hoveredSlot.container instanceof ResultContainer;
        if (!isOutput) return;

        List<Component> tooltip = cir.getReturnValue();
        int req = Utils.getRequiredLevel(stack);
        if (req > 0) {
            LocalPlayer player = Minecraft.getInstance().player;
            boolean met = player != null && player.experienceLevel >= req;

            AbstractContainerMenu screenMenu = ((AbstractContainerScreen)(Object)this).getMenu();
            boolean isCraftingMenu = screenMenu instanceof CraftingMenu || screenMenu instanceof InventoryMenu
                    || screenMenu instanceof SmithingMenu;

            Utils.addToTooltip(tooltip, req, met, isCraftingMenu);
        }
    }

}