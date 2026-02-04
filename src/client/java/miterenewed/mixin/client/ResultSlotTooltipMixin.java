package miterenewed.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
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
    private void mite$addResultOnlyTooltip(ItemStack stack, CallbackInfoReturnable<List<Component>> cir) {
        if (this.hoveredSlot == null) return;

        boolean isOutput = this.hoveredSlot instanceof ResultSlot ||
                this.hoveredSlot.container instanceof ResultContainer;
        if (!isOutput) return;

        List<Component> tooltip = cir.getReturnValue();
        int req = getRequiredLevel(stack);
        if (req > 0) {
            LocalPlayer player = Minecraft.getInstance().player;
            boolean met = player != null && player.experienceLevel >= req;

            tooltip.add(Component.empty());

            ChatFormatting color = met ? ChatFormatting.GREEN : ChatFormatting.RED;
            tooltip.add(Component.literal("⚒ FORGE KNOWLEDGE").withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));
            tooltip.add(Component.literal("Requires: Level " + req).withStyle(color));

            if (!met) {
                tooltip.add(Component.literal("(!) Insufficient level to craft or repair")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC));
            }
        }
    }

    private int getRequiredLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        boolean isEnchantable = stack.has(DataComponents.ENCHANTABLE);
        if (isEnchantable) {
            if (stack.getDisplayName().getString().toLowerCase().contains("netherite")) return 35;
            if (stack.getDisplayName().getString().toLowerCase().contains("diamond")) return 20;
            if (stack.getDisplayName().getString().toLowerCase().contains("gold")) return 12;
            if (stack.getDisplayName().getString().toLowerCase().contains("iron")) return 10;
            if (stack.getDisplayName().getString().toLowerCase().contains("copper")) return 5;
        }
        return 0;
    }
}