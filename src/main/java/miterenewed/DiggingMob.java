package miterenewed;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface DiggingMob {
    @Nullable BlockPos getDiggingTarget();
    void setDiggingTarget(BlockPos pos);
}
