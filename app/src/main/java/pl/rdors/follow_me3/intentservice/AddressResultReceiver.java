package pl.rdors.follow_me3.intentservice;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.view.View;

import pl.rdors.follow_me3.view.ViewElements;
import pl.rdors.follow_me3.view.ViewElementsManager;
import pl.rdors.follow_me3.utils.AppUtils;

/**
 * Receiver for data sent from FetchAddressIntentService.
 */
public class AddressResultReceiver extends ResultReceiver {

    private String address;
    private String area;
    private String city;
    private String street;

    private ViewElements viewElements;

    public AddressResultReceiver(Handler handler, ViewElements viewElements) {
        super(handler);
        this.viewElements = viewElements;
    }

    /**
     * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
     */
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        address = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);
        area = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);
        city = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
        street = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

        displayAddressOutput();
    }

    /**
     * Updates the address in the UI.
     */
    private void displayAddressOutput() {
        viewElements.handleLocation(street);
    }

}