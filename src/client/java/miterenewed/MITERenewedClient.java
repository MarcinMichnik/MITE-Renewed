package miterenewed;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MITERenewedClient implements ClientModInitializer {
	boolean isToggleEnabled = false;
	public void onInitializeClient() {
		// TODO - Sprint toggle on TAB key press
		//ModKeyBindings.register();
		//registerSprintToggle();
	}

	private void registerSprintToggle() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (ModKeyBindings.TOGGLE_SPRINT.wasPressed()) {
				isToggleEnabled = !isToggleEnabled;
			}

			if (isToggleEnabled && client.player != null) {
				client.player.setSprinting(true);
			}
		});
	}
}