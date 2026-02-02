package miterenewed.mixin;

import miterenewed.ModConstants;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StructurePlacement.class)
public abstract class VillageGenerationLimiterMixin {

    @Shadow
    public abstract int salt();

    @Inject(method = "isStructureChunk", at = @At("HEAD"), cancellable = true)
    private void gateVillageSpawn(ChunkGeneratorStructureState context, int x, int z,
                                  CallbackInfoReturnable<Boolean> cir) {
        if (salt() != 10387312) { // 10387312 salt points to Village structures
            return;
        }
        int minimumBlockDistance = ModConstants.MIN_DISTANCE_VILLAGE_GENERATION;
        long minDistanceInChunkSize = (minimumBlockDistance * minimumBlockDistance) / 256;
        long xPosSquared = (long) x * x;
        long zPosSquared = (long) z * z;
        if (xPosSquared + zPosSquared < minDistanceInChunkSize) {
            cir.setReturnValue(false);
        }
    }
}