package pl.rdors.follow_me3;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

/**
 * Receiver for data sent from FetchAddressIntentService.
 */
class AddressResultReceiver extends ResultReceiver {

    /**
     * The formatted location address.
     */
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;

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

        // Display the address string or an error message sent from the intent service.
        mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

        mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

        mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
        mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

        displayAddressOutput();

        // Show a toast message if an address was found.
        if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
            //  showToast(getString(R.string.address_found));
        }
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAreaOutput != null)
                // mLocationText.setText(mAreaOutput+ "");
                textViewTool.getLocationAddress().setText(mAddressOutput);
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}