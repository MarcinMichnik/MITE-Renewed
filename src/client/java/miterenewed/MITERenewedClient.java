package miterenewed;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MITERenewedClient implements ClientModInitializer {
	public boolean isToggleEnabled = false;
	public void onInitializeClient() {
		ModKeyBindings.register();
		SprintToggle.getSprintToggle().registerSprintToggle();
		ZoomToggle.getZoomToggle().registerZoomToggle();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			AutoMineManager.update();
		});
	}

}