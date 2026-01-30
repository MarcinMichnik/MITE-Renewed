package miterenewed.mixin;


import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MiningSpeedLimiterMixin {

    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void slowDownMiningSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        float originalSpeed = cir.getReturnValue();
        Player player = (Player) (Object) this;

        // Wood logs breakable only with axes
        if (block.is(BlockTags.LOGS)) {
            if (!player.getMainHandItem().is(ItemTags.AXES)) {
                cir.setReturnValue(0f);
                return;
            }
        }

        if (player.getMainHandItem().is(Items.WOODEN_PICKAXE) ||
                player.getMainHandItem().is(Items.WOODEN_AXE) ||
                player.getMainHandItem().is(Items.STONE_PICKAXE) ||
                player.getMainHandItem().is(Items.STONE_AXE)) {
            cir.setReturnValue(0f);
            return;
        }

        // 1. Check if the player is using an effective tool
        boolean isEffective = player.getMainHandItem().isCorrectToolForDrops(block);

        // 4x slower with the right tool; 10x slower with wrong tool or by hand
        float modifier = isEffective ? 0.25f : 0.1f;

        cir.setReturnValue(originalSpeed * modifier);
    }
}