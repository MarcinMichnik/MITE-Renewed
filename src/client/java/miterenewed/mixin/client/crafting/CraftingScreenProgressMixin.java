package miterenewed.mixin.client.crafting;

import miterenewed.CraftingTimer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fills the crafting table arrow with the crafting progress, like the furnace's smelting arrow. */
@Mixin(CraftingScreen.class)
public abstract class CraftingScreenProgressMixin extends AbstractRecipeBookScreen<CraftingMenu> {
    // The crafting table arrow has the same shape as the furnace one, so the furnace sprite fits it exactly
    @Unique private static final Identifier PROGRESS_SPRITE = Identifier.withDefaultNamespace("container/furnace/burn_progress");
    @Unique private static final int ARROW_X = 89;
    @Unique private static final int ARROW_Y = 34;
    @Unique private static final int ARROW_WIDTH = 24;
    @Unique private static final int ARROW_HEIGHT = 16;

    protected CraftingScreenProgressMixin(CraftingMenu menu, RecipeBookComponent<?> recipeBook, Inventory inventory, Component title) {
        super(menu, recipeBook, inventory, title);
    }

    @Inject(method = "extractBackground", at = @At("TAIL"))
    private void mite$drawCraftingProgress(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!(this.menu instanceof CraftingTimer timer) || timer.mite$getCraftDuration() <= 0) {
            return;
        }
        float progress = (float) timer.mite$getCraftProgress() / timer.mite$getCraftDuration();
        int width = Mth.ceil(Mth.clamp(progress, 0.0F, 1.0F) * ARROW_WIDTH);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PROGRESS_SPRITE, ARROW_WIDTH, ARROW_HEIGHT, 0, 0,
                this.leftPos + ARROW_X, this.topPos + ARROW_Y, width, ARROW_HEIGHT);
    }
}
