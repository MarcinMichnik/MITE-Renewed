package miterenewed.mixin.crafting;

import miterenewed.CraftingTimer;
import miterenewed.CraftingTimerDataSlot;
import miterenewed.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Crafting takes time. Clicking the result starts the craft; the server measures time from that
 * click, and a changed or taken result goes back to waiting for a click. The client only shows
 * the synced values.
 */
@Mixin(AbstractCraftingMenu.class)
public abstract class CraftingTimeMixin extends RecipeBookMenu implements CraftingTimer {
    @Shadow @Final protected CraftingContainer craftSlots;
    @Shadow @Final protected ResultContainer resultSlots;

    @Shadow protected abstract Player owner();

    @Unique private ItemStack lastResult = ItemStack.EMPTY;
    @Unique private boolean crafting;
    @Unique private long craftStartTime;
    @Unique private int duration;
    @Unique private int syncedProgress;
    @Unique private int syncedDuration;

    protected CraftingTimeMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void mite$addCraftingTimeSync(CallbackInfo ci) {
        this.addDataSlot(new CraftingTimerDataSlot(this, CraftingTimerDataSlot.PROGRESS));
        this.addDataSlot(new CraftingTimerDataSlot(this, CraftingTimerDataSlot.DURATION));
    }

    @Override
    public int mite$getCraftProgress() {
        // owner() is null while a subclass constructor is still running
        Player player = owner();
        if (player == null || player.level().isClientSide()) {
            return syncedProgress;
        }
        mite$update(player);
        if (!crafting) {
            // Waiting for the click (a zero duration, e.g. in creative, still counts as ready)
            return 0;
        }
        return (int) Math.min(player.level().getGameTime() - craftStartTime, duration);
    }

    @Override
    public int mite$getCraftDuration() {
        Player player = owner();
        if (player == null || player.level().isClientSide()) {
            return syncedDuration;
        }
        mite$update(player);
        return duration;
    }

    @Override
    public void mite$setSyncedCraftData(int index, int value) {
        if (index == CraftingTimerDataSlot.PROGRESS) {
            syncedProgress = value;
        } else {
            syncedDuration = value;
        }
    }

    @Override
    public void mite$startCraft() {
        Player player = owner();
        if (player == null || player.level().isClientSide()) {
            return;
        }
        mite$update(player);
        ItemStack result = resultSlots.getItem(0);
        // Items the player isn't skilled enough for can't be crafted at all
        if (!crafting && !result.isEmpty() && player.experienceLevel >= Utils.getRequiredLevel(result)) {
            crafting = true;
            craftStartTime = player.level().getGameTime();
        }
    }

    @Override
    public void mite$tickCraft() {
        Player player = owner();
        if (player == null || player.level().isClientSide() || !crafting || !mite$isCraftReady()) {
            return;
        }
        Slot result = ((AbstractCraftingMenu) (Object) this).getResultSlot();
        // Same checks a click goes through; if the inventory is full the item stays for the player to take by hand
        if (result.hasItem() && result.mayPickup(player)
                && player.experienceLevel >= Utils.getRequiredLevel(result.getItem())) {
            ItemStack crafted = result.getItem().copy();
            // Shift-click logic: crafts once, moves the item into the inventory and calls onTake
            this.quickMoveStack(player, result.index);
            // onTake restarted the timer; if the leftover ingredients make the same item again, keep crafting
            if (!crafting && ItemStack.isSameItemSameComponents(result.getItem(), crafted)) {
                mite$startCraft();
            }
        }
    }

    @Override
    public void mite$restartCraft() {
        crafting = false;
        syncedProgress = 0;
    }

    /** A different result (or count) in the output slot cancels the craft and waits for a new click. */
    @Unique
    private void mite$update(Player player) {
        ItemStack result = resultSlots.getItem(0);
        if (ItemStack.matches(result, lastResult)) {
            return;
        }
        lastResult = result.copy();
        crafting = false;
        duration = player.hasInfiniteMaterials() ? 0 : Utils.getCraftingTicks(result, mite$countIngredients());
    }

    @Unique
    private int mite$countIngredients() {
        int count = 0;
        for (int i = 0; i < craftSlots.getContainerSize(); i++) {
            if (!craftSlots.getItem(i).isEmpty()) {
                count++;
            }
        }
        return count;
    }
}
