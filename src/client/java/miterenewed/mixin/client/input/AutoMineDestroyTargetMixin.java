package miterenewed.mixin.client.input;

import miterenewed.AutoMineManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class AutoMineDestroyTargetMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private BlockPos destroyBlockPos;

    @Shadow
    private ItemStack destroyingItem;

    // Vanilla treats a changed main hand item as a new target and restarts mining from zero.
    // While auto-mining, keep the progress and continue with the new tool instead.
    @Inject(method = "sameDestroyTarget", at = @At("HEAD"), cancellable = true)
    private void keepTargetOnToolSwap(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (AutoMineManager.isAutoMineActive() && pos.equals(this.destroyBlockPos) && this.minecraft.player != null) {
            this.destroyingItem = this.minecraft.player.getMainHandItem();
            cir.setReturnValue(true);
        }
    }
}
