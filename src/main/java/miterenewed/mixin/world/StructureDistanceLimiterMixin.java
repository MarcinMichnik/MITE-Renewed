package miterenewed.mixin.world;

import miterenewed.ModConstants;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.AbstractSpreadingStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Keeps selected structures from generating near the world origin (where spawn is), so players
 * have to travel to find them. Structures are identified by the placement salt of their
 * structure set (data/minecraft/worldgen/structure_set/*.json).
 */
@Mixin(AbstractSpreadingStructurePlacement.class)
public abstract class StructureDistanceLimiterMixin {
    @Unique private static final int VILLAGES_SALT = 10387312;
    @Unique private static final int PILLAGER_OUTPOSTS_SALT = 165745296;

    @Unique private static final int WOODLAND_MANSION_SALT = 10387319;

    // Structures with loot strong enough to skip the early game
    @Unique private static final int ABANDONED_CAMPS_SALT = 91231127;
    @Unique private static final int DESERT_PYRAMIDS_SALT = 14357617;
    @Unique private static final int IGLOOS_SALT = 14357618;
    @Unique private static final int JUNGLE_TEMPLES_SALT = 14357619;
    @Unique private static final int OCEAN_RUINS_SALT = 14357621;
    @Unique private static final int SHIPWRECKS_SALT = 165745295;
    @Unique private static final int RUINED_PORTALS_SALT = 34222645;
    @Unique private static final int TRIAL_CHAMBERS_SALT = 94251327;
    @Unique private static final int TRAIL_RUINS_SALT = 83469867;
    @Unique private static final int ANCIENT_CITIES_SALT = 20083232;
    @Unique private static final int OCEAN_MONUMENTS_SALT = 10387313; // shared with end cities, which are far out anyway

    @Shadow
    protected abstract int salt();

    @Inject(method = "isStructureChunk", at = @At("HEAD"), cancellable = true)
    private void mite$keepAwayFromSpawn(ChunkGeneratorStructureState context, int chunkX, int chunkZ,
                                        CallbackInfoReturnable<Boolean> cir) {
        int minBlockDistance = switch (salt()) {
            case VILLAGES_SALT -> ModConstants.MIN_DISTANCE_VILLAGE_GENERATION;
            case PILLAGER_OUTPOSTS_SALT, WOODLAND_MANSION_SALT -> ModConstants.MIN_DISTANCE_PILLAGER_OUTPOST_GENERATION;
            case ABANDONED_CAMPS_SALT, DESERT_PYRAMIDS_SALT, IGLOOS_SALT, JUNGLE_TEMPLES_SALT, OCEAN_RUINS_SALT,
                 SHIPWRECKS_SALT, RUINED_PORTALS_SALT, TRIAL_CHAMBERS_SALT, TRAIL_RUINS_SALT, ANCIENT_CITIES_SALT,
                 OCEAN_MONUMENTS_SALT -> ModConstants.MIN_DISTANCE_LOOT_STRUCTURE_GENERATION;
            default -> 0;
        };
        if (minBlockDistance <= 0) {
            return;
        }
        // Compare in chunks: (blocks / 16)^2 = blocks^2 / 256
        long minDistanceInChunksSquared = ((long) minBlockDistance * minBlockDistance) / 256;
        if ((long) chunkX * chunkX + (long) chunkZ * chunkZ < minDistanceInChunksSquared) {
            cir.setReturnValue(false);
        }
    }
}
