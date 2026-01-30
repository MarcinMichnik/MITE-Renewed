package miterenewed.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(targets = "net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase")
public abstract class MiningSpeedPerLevelMixin {

    @Inject(method = "getDestroyProgress", at = @At("RETURN"), cancellable = true)
    private void applyFinalLevelBonus(Player player, BlockGetter world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        float originalProgress = cir.getReturnValue();

        if (originalProgress <= 0.0f) return;

        // Calculate 2% bonus per level
        float multiplier = 1.0f + (player.experienceLevel * 0.02f);
        cir.setReturnValue(originalProgress * multiplier);
    }
}