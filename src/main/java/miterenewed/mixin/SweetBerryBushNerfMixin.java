package miterenewed.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SweetBerryBushBlock.class)
public class SweetBerryBushNerfMixin {

    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;"),
            locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void randomizeDrop(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        int age = state.getValue(SweetBerryBushBlock.AGE);
        int count = 0;
        if (age >= 3) {
            count = 1;
        } else if (age > 1) {
            count = world.random.nextInt(2);
        }
        if (age > 1) {
            if (count > 0) {
                Block.popResource(world, pos, new ItemStack(Items.SWEET_BERRIES, count));
                world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            } else {
                // Play a different sound or just the "failure" sound to indicate no berries were found
                world.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PLACE, SoundSource.BLOCKS, 0.5F, 1.5F);
            }

            BlockState newState = state.setValue(SweetBerryBushBlock.AGE, 1);
            world.setBlock(pos, newState, 2);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));

            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}