package miterenewed.mixin.block;

import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Blocks blown up by a creeper are destroyed without dropping as items.
 * Shulker boxes are exempt, since their contents only survive by dropping the box itself.
 */
@Mixin(Block.class)
public abstract class CreeperExplosionNoDropsMixin {
    @Inject(method = "dropFromExplosion", at = @At("HEAD"), cancellable = true)
    private void mite$noCreeperDrops(Explosion explosion, CallbackInfoReturnable<Boolean> cir) {
        if (explosion.getDirectSourceEntity() instanceof Creeper && !((Object) this instanceof ShulkerBoxBlock)) {
            cir.setReturnValue(false);
        }
    }
}
