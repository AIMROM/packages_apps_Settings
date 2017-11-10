package com.google.android_quick_settings;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.util.StringBuilderPrinter;
import java.util.Locale;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.N)
public class FreedomHubQS
   extends TileService {

    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    private static final String PREFERENCES_KEY = "com.google.android_quick_settings";

    /**
     * Called when the tile is added to the Quick Settings.
     * @return TileService constant indicating tile state
     */

    @Override
    public void onTileAdded() {
        Log.d("QS", "Tile added");
    }

    /**
     * Called when this tile begins listening for events.
     */
    @Override
    public void onStartListening() {
        Log.d("QS", "Start listening");
    }

    /**
     * Called when the user taps the tile.
     */
    @Override
    public void onClick() {
        Log.d("QS", "Tile tapped");

        updateTile();
		 // Check to see if the device is currently locked.
        boolean isCurrentlyLocked = this.isLocked();

        if (!isCurrentlyLocked) {

            Resources resources = getApplication().getResources();

            Tile tile = getQsTile();
            String tileLabel = tile.getLabel().toString();
            String tileState = (tile.getState() == Tile.STATE_ACTIVE) ?
                    resources.getString(R.string.service_active) :
                    resources.getString(R.string.service_inactive);

            Intent intent = new Intent(getApplicationContext(),
                    Settings.freedomhubSettingsActivity.class);
            startActivityAndCollapse(intent);
        }
    }

    /**
     * Called when this tile moves out of the listening state.
     */
    @Override
    public void onStopListening() {
        Log.d("QS", "Stop Listening");
    }

    /**
     * Called when the user removes this tile from Quick Settings.
     */
    @Override
    public void onTileRemoved() {
        Log.d("QS", "Tile removed");
    }

    // Changes the appearance of the tile.
    private void updateTile() {

        Tile tile = this.getQsTile();
        boolean isActive = getServiceStatus();

        Icon newIcon;
        String newLabel;
        int newState;

        // Change the tile to match the service status.
        if (isActive) {

            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.freedomhub_title),
                    getString(R.string.service_active));

            newIcon = Icon.createWithResource(getApplicationContext(),
                    R.drawable.ic_settings_freedomhub);

            newState = Tile.STATE_ACTIVE;

        } else {
            newLabel = String.format(Locale.US,
                    "%s %s",
                    getString(R.string.freedomhub_title),
                    getString(R.string.service_inactive));

            newIcon =
                    Icon.createWithResource(getApplicationContext(),
                            R.drawable.ic_settings_freedomhub);

            newState = Tile.STATE_INACTIVE;
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);

        // Need to call updateTile for the tile to pick up changes.
        tile.updateTile();
    }

    // Access storage to see how many times the tile
    // has been tapped.
    private boolean getServiceStatus() {

        SharedPreferences prefs =
                getApplicationContext()
                        .getSharedPreferences(PREFERENCES_KEY,
                                MODE_PRIVATE);

        boolean isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        isActive = !isActive;

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();

        return isActive;
    }
}