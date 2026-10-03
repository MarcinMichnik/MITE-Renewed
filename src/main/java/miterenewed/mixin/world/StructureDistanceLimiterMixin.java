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

    @Shadow
    protected abstract int salt();

    @Inject(method = "isStructureChunk", at = @At("HEAD"), cancellable = true)
    private void mite$keepAwayFromSpawn(ChunkGeneratorStructureState context, int chunkX, int chunkZ,
                                        CallbackInfoReturnable<Boolean> cir) {
        int minBlockDistance = switch (salt()) {
            case VILLAGES_SALT -> ModConstants.MIN_DISTANCE_VILLAGE_GENERATION;
            case PILLAGER_OUTPOSTS_SALT, WOODLAND_MANSION_SALT -> ModConstants.MIN_DISTANCE_PILLAGER_OUTPOST_GENERATION;
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
