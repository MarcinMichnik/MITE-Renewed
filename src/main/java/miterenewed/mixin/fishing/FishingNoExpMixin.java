package miterenewed.mixin.fishing;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Reeling in a catch spawns the caught item and a 1-6 XP orb; only the item is kept. */
@Mixin(FishingHook.class)
public abstract class FishingNoExpMixin {
    @WrapOperation(method = "retrieve", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean mite$skipExperienceOrb(Level level, Entity entity, Operation<Boolean> original) {
        if (entity instanceof ExperienceOrb) {
            return false;
        }
        return original.call(level, entity);
    }
}
