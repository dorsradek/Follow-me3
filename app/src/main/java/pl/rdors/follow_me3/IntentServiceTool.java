package pl.rdors.follow_me3;

import android.content.Intent;
import android.location.Location;

/**
 * Created by rdors on 2016-11-02.
 */

public class IntentServiceTool {

    private AddressResultReceiver addressResultReceiver;
    private TestActivity activity;

    public IntentServiceTool(AddressResultReceiver addressResultReceiver, TestActivity activity) {
        this.addressResultReceiver = addressResultReceiver;
        this.activity = activity;
    }

    public void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(activity, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, addressResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        activity.startService(intent);
    }
}
