package miterenewed.mixin.player;

import miterenewed.ModConstants;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPunchPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class AttackSwingExhaustionMixin {
    @Shadow public ServerPlayer player;

    // Server tick of the last entity hit or block-break start; the client follows both with a punch packet
    @Unique private int miteHandledSwingTick = -1;

    // Swing that hits an entity (vanilla Player.attack adds its own 0.1 only when damage is dealt)
    @Inject(method = "handleAttack", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;attack(Lnet/minecraft/world/entity/Entity;)V"))
    private void exhaustOnHitSwing(ServerboundAttackPacket packet, CallbackInfo ci) {
        this.player.causeFoodExhaustion(ModConstants.EXHAUSTION_ON_SWING);
        this.miteHandledSwingTick = this.player.level().getServer().getTickCount();
    }

    @Inject(method = "handlePlayerAction", at = @At("RETURN"))
    private void markBlockPunch(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        if (packet.getAction() == ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK) {
            this.miteHandledSwingTick = this.player.level().getServer().getTickCount();
        }
    }

    // Swing at air. The client also sends a punch packet after an entity hit, when it starts
    // breaking a block and every tick while left click is held on a block - those are skipped.
    @Inject(method = "handlePunch", at = @At("TAIL"))
    private void exhaustOnMissSwing(ServerboundPunchPacket packet, CallbackInfo ci) {
        if (this.miteHandledSwingTick == this.player.level().getServer().getTickCount()) {
            return;
        }
        if (this.player.pick(this.player.blockInteractionRange(), 1.0F, false).getType() == HitResult.Type.BLOCK) {
            return;
        }
        this.player.causeFoodExhaustion(ModConstants.EXHAUSTION_ON_SWING);
    }
}
