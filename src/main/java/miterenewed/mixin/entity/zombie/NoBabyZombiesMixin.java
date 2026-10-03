package miterenewed.mixin.entity.zombie;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Zombies (and husks, drowned, zombie villagers, zombified piglins) never spawn as babies,
 * which also means no chicken jockeys. Babies from other sources are turned into adults on load
 * (see MITERenewed).
 */
@Mixin(Zombie.class)
public abstract class NoBabyZombiesMixin {
    @Inject(method = "getSpawnAsBabyOdds", at = @At("HEAD"), cancellable = true)
    private static void mite$neverSpawnAsBaby(RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
