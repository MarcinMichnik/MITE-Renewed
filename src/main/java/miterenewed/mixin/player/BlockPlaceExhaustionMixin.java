package miterenewed.mixin.player;

import miterenewed.ModConstants;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockPlaceExhaustionMixin {
    @Inject(method = "place", at = @At("RETURN"))
    private void exhaustOnPlace(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Player player = context.getPlayer();
        if (player != null && !context.getLevel().isClientSide() && cir.getReturnValue().consumesAction()) {
            player.causeFoodExhaustion(ModConstants.EXHAUSTION_ON_BLOCK_PLACE);
        }
    }
}
