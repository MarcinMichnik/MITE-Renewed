package miterenewed.mixin;


import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MiningSpeedLimiterMixin {

    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void slowDownMiningSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        float originalSpeed = cir.getReturnValue();
        PlayerEntity player = (PlayerEntity) (Object) this;

        // 1. Check if the player is using an effective tool
        boolean isEffective = player.getMainHandStack().isSuitableFor(block);

        // 2x slower with the right tool; 10x slower with wrong tool or by hand
        float modifier = isEffective ? 0.5f : 0.1f;

        cir.setReturnValue(originalSpeed * modifier);
    }
}