package miterenewed.mixin;

import miterenewed.DiggingMob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Zombie.class)
public abstract class ZombieDigTracker implements DiggingMob {
    @Nullable
    private BlockPos diggingTarget;

    @Override
    public BlockPos getDiggingTarget() {
        return diggingTarget;
    }

    public void setDiggingTarget(@Nullable BlockPos pos) {
        this.diggingTarget = pos;
    }
}
