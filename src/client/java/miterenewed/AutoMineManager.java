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

        boolean keyPressed = mc.mouseHandler.isLeftPressed() && mc.mouseHandler.isRightPressed();
        if (keyPressed && !keyWasPressed) {
            autoMineActive = !autoMineActive;
            sendToggleMessage(player);
        }

        // Simulate holding left click when active
        if (!autoMineActive) {
            if (mc.level != null && keyWasPressed && mc.hitResult instanceof BlockHitResult blockHit) {
                mc.level.destroyBlockProgress(mc.player.getId(), blockHit.getBlockPos(), -1);
                if (mc.gameMode != null) {
                    // Workaround to reset block destruction progress
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

    public static boolean isAutoMineActive() {
        return autoMineActive;
    }

}