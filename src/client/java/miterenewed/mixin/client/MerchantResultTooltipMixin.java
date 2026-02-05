package miterenewed.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class MerchantResultTooltipMixin {

    @Inject(
            method = "renderTooltip(Lnet/minecraft/client/gui/GuiGraphics;II)V",
            at = @At("TAIL")
    )
    private void mite$appendXpRequirement(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        Slot hoveredSlot = ((MerchantScreenAccessor) screen).getHoveredSlot();

        if (screen instanceof MerchantScreen && hoveredSlot != null && hoveredSlot.getContainerSlot() == 2 && hoveredSlot.hasItem()) {
            int totalCost = 50;

            List<ClientTooltipComponent> tooltipData = new ArrayList<>();

            tooltipData.add(ClientTooltipComponent.create(Component.empty().getVisualOrderText()));

            Component title = Component.literal("⚒ KNOWLEDGE REQUIREMENT")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
            tooltipData.add(ClientTooltipComponent.create(title.getVisualOrderText()));

            Player player = Minecraft.getInstance().player;
            boolean canAfford = player != null && player.totalExperience >= totalCost;
            ChatFormatting color = canAfford ? ChatFormatting.GREEN : ChatFormatting.RED;

            Component costText = Component.literal("Total Cost: " + totalCost + " XP").withStyle(color);
            tooltipData.add(ClientTooltipComponent.create(costText.getVisualOrderText()));

            if (!canAfford) {
                Component warning = Component.literal("(!) Insufficient Experience")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC);
                tooltipData.add(ClientTooltipComponent.create(warning.getVisualOrderText()));
            }

            guiGraphics.renderTooltip(
                    Minecraft.getInstance().font,
                    tooltipData,
                    mouseX,
                    mouseY,
                    DefaultTooltipPositioner.INSTANCE,
                    null
            );
        }
    }
}