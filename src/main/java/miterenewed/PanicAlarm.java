package miterenewed;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

/** Implemented on PanicGoal: lets a herd member raise the alarm on its neighbours. */
public interface PanicAlarm {
    void mite$alarm(@Nullable LivingEntity threat);
}
