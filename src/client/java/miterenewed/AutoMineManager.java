package miterenewed;


import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;

public class AutoMineManager {
    private static boolean autoMineActive = false;
    private static boolean keyWasPressed = false;

    public static void update() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;

        // Toggle on key press
        boolean keyPressed = ModKeyBindings.AUTO_MINE_TOGGLE.isDown();
        if (keyPressed && !keyWasPressed) {
            autoMineActive = !autoMineActive;
            sendToggleMessage(player);
        }

        // Simulate holding left click when active
        if (autoMineActive) {
            // This ensures left click is "held" even if player releases mouse button
            mc.options.keyAttack.setDown(true);
        } else {

            // Reset to normal behavior
            mc.options.keyAttack.setDown(mc.mouseHandler.isLeftPressed());
            if (mc.level != null && keyWasPressed && mc.hitResult instanceof BlockHitResult blockHit) {
                mc.level.destroyBlockProgress(mc.player.getId(), blockHit.getBlockPos(), -1);
                if (mc.gameMode != null) {
                    mc.gameMode.startDestroyBlock(blockHit.getBlockPos(), blockHit.getDirection());
                }
            }
        }
        keyWasPressed = keyPressed;
    }

    private static void sendToggleMessage(LocalPlayer player) {
        String status = autoMineActive ? "§aON" : "§cOFF";
        player.displayClientMessage(Component.literal("Auto-mine: " + status), true);
    }

}