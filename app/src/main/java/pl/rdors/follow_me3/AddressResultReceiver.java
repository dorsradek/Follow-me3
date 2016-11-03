package pl.rdors.follow_me3;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.view.View;

/**
 * Receiver for data sent from FetchAddressIntentService.
 */
class AddressResultReceiver extends ResultReceiver {

    protected String address;
    protected String area;
    protected String city;
    protected String street;

    private TextViewTool textViewTool;

    public AddressResultReceiver(Handler handler, TextViewTool textViewTool) {
        super(handler);
        this.textViewTool = textViewTool;
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
        try {
            textViewTool.getLocationAddress().setVisibility(View.VISIBLE);
            textViewTool.getLocationMarkerText().setVisibility(View.VISIBLE);
            if (street != null)
                textViewTool.getLocationAddress().setText(street);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}