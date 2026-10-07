package miterenewed.mixin.client.crafting;

import miterenewed.CraftingTimer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fills the small 2x2 crafting arrow in the inventory with the crafting progress. There is no
 * sprite of that size, so the arrow is painted column by column over its outline in the texture.
 */
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenProgressMixin extends AbstractRecipeBookScreen<InventoryMenu> {
    @Unique private static final int ARROW_X = 135;
    @Unique private static final int ARROW_SHAFT_END_X = 143;
    @Unique private static final int ARROW_TIP_X = 150;
    @Unique private static final int ARROW_CENTER_Y = 35;
    @Unique private static final int SHAFT_HALF_HEIGHT = 1;
    @Unique private static final int HEAD_HALF_HEIGHT = 6;
    @Unique private static final int FILL_COLOR = 0xFFFFFFFF;

    protected InventoryScreenProgressMixin(InventoryMenu menu, RecipeBookComponent<?> recipeBook, Inventory inventory, Component title) {
        super(menu, recipeBook, inventory, title);
    }

    @Inject(method = "extractBackground", at = @At("TAIL"))
    private void mite$drawCraftingProgress(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!(this.menu instanceof CraftingTimer timer) || timer.mite$getCraftDuration() <= 0) {
            return;
        }
        float progress = Mth.clamp((float) timer.mite$getCraftProgress() / timer.mite$getCraftDuration(), 0.0F, 1.0F);
        int arrowWidth = ARROW_TIP_X - ARROW_X + 1;
        int filledColumns = Mth.ceil(progress * arrowWidth);
        for (int i = 0; i < filledColumns; i++) {
            int x = ARROW_X + i;
            // Shaft is 3 pixels tall; the head starts 13 tall and narrows by one pixel per side per column
            int halfHeight = x <= ARROW_SHAFT_END_X ? SHAFT_HALF_HEIGHT : HEAD_HALF_HEIGHT - (x - ARROW_SHAFT_END_X - 1);
            int left = this.leftPos + x;
            int top = this.topPos + ARROW_CENTER_Y - halfHeight;
            graphics.fill(left, top, left + 1, this.topPos + ARROW_CENTER_Y + halfHeight + 1, FILL_COLOR);
        }
    }
}
