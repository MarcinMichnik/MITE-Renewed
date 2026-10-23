package miterenewed.mixin.world;

import miterenewed.ModConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;
import java.util.Set;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Keeps dangerous mobs from spawning naturally near the world origin (where spawn is), using the
 * same distance rule as StructureDistanceLimiterMixin.
 */
@Mixin(SpawnPlacements.class)
public abstract class MobSpawnDistanceLimiterMixin {
    @Unique
    private static final Set<EntityType<?>> KEPT_AWAY_FROM_SPAWN = Set.of(
        EntityTypes.ZOMBIE_HORSE, // always comes with a spear-wielding zombie rider
        EntityTypes.WITCH,
        EntityTypes.HUSK,         // can come riding a camel husk with a parched, holding an iron spear
        EntityTypes.DROWNED,      // can come riding a zombie nautilus, holding a trident
        EntityTypes.BOGGED        // poison arrows
    );

    @Inject(method = "checkSpawnRules", at = @At("HEAD"), cancellable = true)
    private static <T extends Entity> void mite$keepAwayFromSpawn(EntityType<T> type, ServerLevelAccessor level,
                                                                  EntitySpawnReason reason, BlockPos pos,
                                                                  RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        if (reason != EntitySpawnReason.NATURAL || !KEPT_AWAY_FROM_SPAWN.contains(type)) {
            return;
        }
        long minDistance = ModConstants.MIN_DISTANCE_DANGEROUS_MOB_SPAWN;
        if ((long) pos.getX() * pos.getX() + (long) pos.getZ() * pos.getZ() < minDistance * minDistance) {
            cir.setReturnValue(false);
        }
    }
}
