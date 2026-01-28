package miterenewed;

import net.fabricmc.api.ClientModInitializer;

public class MITERenewedClient implements ClientModInitializer {
	public boolean isToggleEnabled = false;
	public void onInitializeClient() {
		ModKeyBindings.register();
		SprintToggle.getSprintToggle().registerSprintToggle();
		ZoomToggle.getZoomToggle().registerZoomToggle();
	}

}