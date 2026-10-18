package miterenewed;


import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;

public class AutoMineManager {
    private static boolean autoMineActive = false;
    private static boolean keyWasPressed = false;

    public static void update() {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || mc.gameMode == null || !ModConstants.AUTO_MINE_ENABLED) return;

        boolean keyPressed = mc.mouseHandler.isLeftPressed() && mc.mouseHandler.isRightPressed();
        if (keyPressed && !keyWasPressed) {
            autoMineActive = !autoMineActive;
            sendToggleMessage(player);
            // No stopDestroyBlock here: switching off while still holding left click keeps the progress,
            // and vanilla stops mining by itself once attack is no longer held
        }

        // Vanilla skips continueAttack for piercing weapons (spears) without ever calling stopDestroyBlock,
        // which would leave the crack overlay and server-side progress hanging
        if (mc.gameMode.isDestroying() && player.getMainHandItem().has(DataComponents.PIERCING_WEAPON)) {
            mc.gameMode.stopDestroyBlock();
        }

        keyWasPressed = keyPressed;
    }

    private static void sendToggleMessage(LocalPlayer player) {
        String status = autoMineActive ? "§aON" : "§cOFF";
        player.sendOverlayMessage(Component.literal("Auto-mine: " + status));
    }

    public static boolean isAutoMineActive() {
        return ModConstants.AUTO_MINE_ENABLED && autoMineActive;
    }

}
