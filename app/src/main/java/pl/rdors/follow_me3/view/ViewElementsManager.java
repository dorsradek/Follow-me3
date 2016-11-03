package pl.rdors.follow_me3.view;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.utils.AppUtils;

/**
 * Created by rdors on 2016-11-02.
 */

public class ViewElementsManager {

    private TextView locationMarkerText;
    private TextView locationAddress;
    private LinearLayout toolbarContainer;

    private TestActivity activity;

    public ViewElementsManager(TestActivity activity, View view) {
        this.activity = activity;
        locationMarkerText = (TextView) view.findViewById(R.id.locationMarkertext);
        locationAddress = (TextView) view.findViewById(R.id.Address);
        toolbarContainer = (LinearLayout) view.findViewById(R.id.container_toolbar);

        locationAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();
            }
        });
    }

    private void openAutocompleteActivity() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(activity);
            activity.startActivityForResult(intent, AppUtils.LocationConstants.REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.getConnectionStatusCode());
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }
    }

    public TextView getLocationMarkerText() {
        return locationMarkerText;
    }

    public TextView getLocationAddress() {
        return locationAddress;
    }

    public LinearLayout getToolbarContainer() {
        return toolbarContainer;
    }
}
