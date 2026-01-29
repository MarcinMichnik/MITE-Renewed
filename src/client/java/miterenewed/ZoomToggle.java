package miterenewed;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public final class ZoomToggle {
    private static boolean isZoomToggleEnabled = false;
    private static ZoomToggle zoomToggle;

    private ZoomToggle() {}

    public static ZoomToggle getZoomToggle() {
        if (zoomToggle == null) {
            zoomToggle = new ZoomToggle();
        }
        return zoomToggle;
    }

    public void registerZoomToggle() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            boolean wasToggleEnabled = isZoomToggleEnabled();
            while (ModKeyBindings.ZOOM_KEY.consumeClick()) {
                isZoomToggleEnabled = !isZoomToggleEnabled;
            }
        });
    }

    public boolean isZoomToggleEnabled() {
        return isZoomToggleEnabled;
    }

}
