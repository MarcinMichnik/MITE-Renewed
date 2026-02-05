package miterenewed.mixin.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets = "net.minecraft.client.gui.screens.inventory.MerchantScreen$TradeOfferButton")
public abstract class TradeOfferButtonTooltipMixin {

    @Shadow @Final private int index;

    @Unique
    private MerchantScreen mite$parentScreen;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mite$captureScreen(MerchantScreen merchantScreen, int i, int j, int k, Button.OnPress onPress, CallbackInfo ci) {
        this.mite$parentScreen = merchantScreen;
    }

    @Inject(method = "renderToolTip", at = @At("TAIL"))
    private void mite$addXpCostToTradeButton(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.mite$parentScreen == null) return;

        MerchantScreen screen = this.mite$parentScreen;

        int scrollOffset = ((MerchantScreenAccessor) screen).getScrollOff();
        int currentTradeIndex = this.index + scrollOffset;

        MerchantOffers offers = screen.getMenu().getOffers();

        if (currentTradeIndex >= 0 && currentTradeIndex < offers.size()) {
            int expCost = 50;

            List<Component> tooltipLines = new ArrayList<>();
            tooltipLines.add(Component.empty());
            tooltipLines.add(Component.literal("⚒ TRADE KNOWLEDGE")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));

            Player player = Minecraft.getInstance().player;
            boolean canAfford = player != null && player.totalExperience >= expCost;
            ChatFormatting color = canAfford ? ChatFormatting.GREEN : ChatFormatting.RED;

            tooltipLines.add(Component.literal("Cost: " + expCost + " XP").withStyle(color));

            if (!canAfford) {
                tooltipLines.add(Component.literal("(!) Insufficient Experience")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.ITALIC));
            }

            List<FormattedCharSequence> formattedLines = tooltipLines.stream()
                    .map(Component::getVisualOrderText)
                    .toList();

//            guiGraphics.setTooltipForNextFrame(
//                    screen.getFont(),
//                    formattedLines,
//                    DefaultTooltipPositioner.INSTANCE,
//                    mouseX,
//                    mouseY,
//                    false
//            );
        }
    }
}