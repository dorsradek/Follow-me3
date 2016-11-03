package pl.rdors.follow_me3;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

/**
 * Created by rdors on 2016-11-02.
 */

public class TextViewTool {

    private TextView locationMarkerText;
    private TextView locationAddress;
//    private TextView locationText;

    public static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private TestActivity activity;

    public TextViewTool(TestActivity activity, View view) {
        this.activity = activity;
        locationMarkerText = (TextView) view.findViewById(R.id.locationMarkertext);
        locationAddress = (TextView) view.findViewById(R.id.Address);
        //locationText = (TextView) view.findViewById(R.id.Locality);

        locationAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();
            }
        });
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(activity);
            activity.startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(activity, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }
    }

    public TextView getLocationMarkerText() {
        return locationMarkerText;
    }


    public TextView getLocationAddress() {
        return locationAddress;
    }


//    public TextView getLocationText() {
//        return locationText;
//    }
}
