package miterenewed;


public final class ModConstants {

    private ModConstants() {}

    // Progression constants
    public static final int BASE_HEARTS = 3;
    public static final int BASE_HUNGER = 3;
    public static final int LEVELS_PER_UPGRADE = 10;

    public static final int REGEN_INTERVAL_TICKS = 2000; // 2000 ticks is 100 seconds
    public static final float REGEN_AMOUNT = 1.0F; // half a heart
    public static final float PASSIVE_EXHAUSTION = 0.005f; // per tick; ~1 hunger every 160 seconds
    public static final float EXHAUSTION_ON_JUMP = 0.8f;
}