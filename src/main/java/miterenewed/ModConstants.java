package miterenewed;


public final class ModConstants {

    private ModConstants() {}

    public static final int BASE_HEARTS = 3;
    public static final int BASE_HUNGER = 3;
    public static final int LEVELS_PER_UPGRADE = 5;
    public static final float CROP_GROWTH_MODIFIER = 0.1f; // 0.1f = 10x slower; 1f = normal
    public static final boolean AUTO_MINE_ENABLED = true; // left+right click toggles holding attack to keep mining
    public static final int MIN_DISTANCE_VILLAGE_GENERATION = 1000; // blocks from world origin
    public static final int MIN_DISTANCE_PILLAGER_OUTPOST_GENERATION = 1000; // blocks from world origin
    public static final int MIN_DISTANCE_LOOT_STRUCTURE_GENERATION = 1000; // camps, temples, shipwrecks, ruined portals...; blocks from world origin
    public static final int CRAFTING_EXP_COST_MODIFIER = 6;
    public static final int TRADE_COST_MODIFIER = 10;
    public static final int REGEN_INTERVAL_TICKS = 1500;
    public static final float REGEN_AMOUNT = 1.0F;
    public static final float PASSIVE_EXHAUSTION = 0.0002f;
    public static final float EXHAUSTION_ON_JUMP = 0.05f;

    // Reach towards entities (blocks) without a tool, weapon or stick in hand; vanilla reach is 3
    public static final double BARE_HAND_ATTACK_RANGE = 2.0;

    // Animal panic (cows, pigs, sheep, chickens...)
    public static final double FARM_ANIMAL_PANIC_SPEED = 0.4; // flee speed shared by cows, pigs, sheep and chickens (vanilla: cow 0.4, chicken 0.35, pig 0.31, sheep 0.29)
    public static final double PANIC_SPEED_MULTIPLIER = 1.14; // other animals: on top of their vanilla panic speed
    public static final int PANIC_DURATION_TICKS = 200; // keep fleeing 10 s after being hit or alarmed
    public static final int PANIC_MAX_DURATION_TICKS = 600; // ...up to 30 s while the attacker stays close
    public static final double PANIC_THREAT_RADIUS = 12.0; // "close" for the rule above, in blocks
    public static final double HERD_ALERT_RADIUS = 10.0; // animals within this range flee too

    public static final float SEED_SATURATION = 0.5f; // saturation per seed or sugar eaten; they give no nutrition

    // Tool durability (vanilla: wood 59, stone 131); applies to all tools and swords of that material
    public static final int WOODEN_TOOL_DURABILITY = 10;
    public static final int STONE_TOOL_DURABILITY = 24;

    // Hostile mobs notice players from this many times their normal follow range
    public static final double MOB_DETECTION_RANGE_MULTIPLIER = 2;

    // Zombie digging
    public static final float ZOMBIE_DIG_MAX_HARDNESS = 1.0f; // dirt, sand, gravel, clay, leaves, glass...; not stone/wood
    public static final float ZOMBIE_DIG_TICKS_PER_HARDNESS = 100f; // dirt (0.5) = 50 ticks
    public static final int ZOMBIE_MIN_DIG_TICKS = 20;
    public static final int ZOMBIE_STUCK_TICKS = 40; // no movement for this long before considering digging
    public static final boolean ZOMBIE_DIG_DROPS_BLOCKS = true;
}