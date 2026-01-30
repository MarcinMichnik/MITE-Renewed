package miterenewed;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.player.LocalPlayer;

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
            }
            LocalPlayer player = client.player;
            if (isToggleEnabled && player.canSprint() && !player.isInShallowWater()) {
                client.player.setSprinting(true);
            } else if (wasToggleEnabled) {
                client.player.setSprinting(false);
            }
        });
    }

    public boolean isToggleEnabled() {
        return isToggleEnabled;
    }

}
