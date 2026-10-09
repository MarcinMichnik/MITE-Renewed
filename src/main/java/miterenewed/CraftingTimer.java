package miterenewed;

/**
 * Implemented on crafting menus (crafting table and the 2x2 inventory grid): clicking the result
 * starts crafting it, and it can only be taken once crafting has finished. Progress is synced to
 * the client through two data slots so the screen can fill its arrow.
 */
public interface CraftingTimer {
    /** Ticks the current result has been crafting, capped at the duration. */
    int mite$getCraftProgress();

    /** Ticks needed to craft the current result; 0 when there is nothing to craft. */
    int mite$getCraftDuration();

    /** Client side: receives the values synced from the server. */
    void mite$setSyncedCraftData(int index, int value);

    /** Server side: the player clicked the result, so start crafting it (no-op if already crafting). */
    void mite$startCraft();

    /** Resets to waiting for the next click, e.g. after the result was taken. */
    void mite$restartCraft();

    /**
     * Server side, every tick: moves a finished result into the player's inventory, then starts
     * crafting the next one if the remaining ingredients still make the same item.
     */
    void mite$tickCraft();

    default boolean mite$isCraftReady() {
        return mite$getCraftProgress() >= mite$getCraftDuration();
    }
}
