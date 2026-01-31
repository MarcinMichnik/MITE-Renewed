package miterenewed.mixin;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetingConditions.class)
public class XRayTargetingMixin {

    @Shadow private boolean checkLineOfSight;

    @Inject(method = "test", at = @At("HEAD"))
    private void disableLineOfSight(ServerLevel serverLevel, @Nullable LivingEntity baseEntity,
                                    LivingEntity targetEntity, CallbackInfoReturnable<Boolean> cir) {
        // If the entity checking is a Zombie, ignore walls
        if (baseEntity instanceof Zombie) {
            this.checkLineOfSight = false;
        }
    }

}