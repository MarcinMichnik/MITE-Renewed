package miterenewed;

import net.minecraft.world.inventory.DataSlot;

/** Syncs one value of a {@link CraftingTimer}: index 0 is the progress, 1 the duration. */
public class CraftingTimerDataSlot extends DataSlot {
    public static final int PROGRESS = 0;
    public static final int DURATION = 1;

    private final CraftingTimer timer;
    private final int index;

    public CraftingTimerDataSlot(CraftingTimer timer, int index) {
        this.timer = timer;
        this.index = index;
    }

    @Override
    public int get() {
        return index == PROGRESS ? timer.mite$getCraftProgress() : timer.mite$getCraftDuration();
    }

    @Override
    public void set(int value) {
        timer.mite$setSyncedCraftData(index, value);
    }
}
