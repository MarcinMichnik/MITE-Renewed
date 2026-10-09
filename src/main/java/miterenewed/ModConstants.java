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
    // Crafting time = base + ticks per ingredient; gear uses a per-ingredient time by material (see Utils.getCraftingTicks)
    public static final int CRAFTING_BASE_TICKS = 52;
    public static final int CRAFTING_TICKS_PER_INGREDIENT = 16;
    public static final int TRADE_COST_MODIFIER = 10;
    public static final int REGEN_INTERVAL_TICKS = 1500;
    public static final float REGEN_AMOUNT = 1.0F;
    public static final float PASSIVE_EXHAUSTION = 0.001f;
    public static final float EXHAUSTION_ON_JUMP = 0.1f;
    public static final float EXHAUSTION_ON_SWING = 0.15f; // every attack swing, hit or miss (on top of vanilla 0.1 for a hit)
    public static final float EXHAUSTION_ON_BLOCK_BREAK = 0.1f; // on top of vanilla 0.005
    public static final float EXHAUSTION_ON_BLOCK_PLACE = 0.05f;

    // Reach without a tool, weapon, stick or bone in the main hand
    public static final double BARE_HAND_ATTACK_RANGE = 2.0; // towards entities, in blocks; vanilla 3
    public static final double BARE_HAND_BLOCK_RANGE = 3.0; // breaking and placing blocks; vanilla 4.5

    // Animal panic (cows, pigs, sheep, chickens...)
    public static final double FARM_ANIMAL_PANIC_SPEED = 0.4; // flee speed shared by cows, pigs, sheep and chickens (vanilla: cow 0.4, chicken 0.35, pig 0.31, sheep 0.29)
    public static final double PANIC_SPEED_MULTIPLIER = 1.14; // other animals: on top of their vanilla panic speed
    public static final int PANIC_DURATION_TICKS = 200; // keep fleeing 10 s after being hit or alarmed
    public static final int PANIC_MAX_DURATION_TICKS = 600; // ...up to 30 s while the attacker stays close
    public static final double PANIC_THREAT_RADIUS = 12.0; // "close" for the rule above, in blocks
    public static final double HERD_ALERT_RADIUS = 10.0; // animals within this range flee too

    public static final float BURNING_MOB_TREE_IGNITE_CHANCE = 0.01f; // per tick while a burning mob is within 1 block of logs/leaves (~5 s on average)

    public static final float SEED_SATURATION = 0.5f; // saturation per seed or sugar eaten; they give no nutrition

    // Tool durability (vanilla: wood 59, stone 131); applies to all tools and swords of that material
    public static final int WOODEN_TOOL_DURABILITY = 10;
    public static final int STONE_TOOL_DURABILITY = 24;

    public static final float WOODEN_SWORD_ATTACK_DAMAGE = 3.0F; // total damage per hit; vanilla 4

    // Hostile mobs notice players from this many times their normal follow range
    public static final double MOB_DETECTION_RANGE_MULTIPLIER = 2;

    // Hostile mobs destroying crops
    public static final int MOB_CROP_SEARCH_INTERVAL_TICKS = 100; // idle mobs look for crops every 5-10 s
    public static final int MOB_CROP_DESTROY_TICKS = 20; // time spent at a crop before destroying it
    public static final boolean MOB_DESTROYED_CROPS_DROP = true; // drop the crop's items (seeds...) like a player breaking it

    // Zombie digging
    public static final float ZOMBIE_DIG_MAX_HARDNESS = 1.0f; // dirt, sand, gravel, clay, leaves, glass...; not stone/wood
    public static final float ZOMBIE_DIG_TICKS_PER_HARDNESS = 100f; // dirt (0.5) = 50 ticks
    public static final int ZOMBIE_MIN_DIG_TICKS = 20;
    public static final int ZOMBIE_STUCK_TICKS = 40; // no movement for this long before considering digging
    public static final boolean ZOMBIE_DIG_DROPS_BLOCKS = true;
}