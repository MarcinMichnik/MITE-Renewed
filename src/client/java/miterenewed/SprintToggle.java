package miterenewed;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public final class SprintToggle {
    private static boolean isToggleEnabled = false;
    private static SprintToggle sprintToggle;

    private SprintToggle() {}

    public static SprintToggle getSprintToggle() {
        if (sprintToggle == null) {
            sprintToggle = new SprintToggle();
        }
        return sprintToggle;
    }

    public void registerSprintToggle() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean wasToggleEnabled = isToggleEnabled();
            while (ModKeyBindings.TOGGLE_SPRINT.consumeClick()) {
                isToggleEnabled = !isToggleEnabled;
                sendToggleMessage(client.player);
            }
            LocalPlayer player = client.player;
            if (isToggleEnabled && player.canSprint() && !player.isInShallowWater()) {
                client.player.setSprinting(true);
            } else if (wasToggleEnabled) {
                client.player.setSprinting(false);
            }
        });
    }

    private static void sendToggleMessage(LocalPlayer player) {
        String status = isToggleEnabled ? "§aON" : "§cOFF";
        player.displayClientMessage(Component.literal("Auto-sprint: " + status), true);
    }

    public boolean isToggleEnabled() {
        return isToggleEnabled;
    }

}
