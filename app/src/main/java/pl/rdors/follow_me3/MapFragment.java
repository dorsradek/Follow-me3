package pl.rdors.follow_me3;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment implements IOnActivityResult {

    private TestActivity activity;
    private TextViewTool textViewTool;
    private MapTool mapTool;
    private IntentServiceTool intentServiceTool;
    private AddressResultReceiver addressResultReceiver;
    private GoogleApiTool googleApiTool;

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);

        textViewTool = new TextViewTool(activity, view);
        addressResultReceiver = new AddressResultReceiver(new Handler(), textViewTool);
        intentServiceTool = new IntentServiceTool(addressResultReceiver, activity);
        mapTool = new MapTool(activity, intentServiceTool, textViewTool);
        googleApiTool = new GoogleApiTool(activity, mapTool);

        mapFragment.getMapAsync(mapTool);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (TestActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            googleApiTool.getGoogleApiClient().connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (googleApiTool.getGoogleApiClient() != null && googleApiTool.getGoogleApiClient().isConnected()) {
            googleApiTool.getGoogleApiClient().disconnect();
        }
    }


    @Override
    public void apply(int requestCode, int resultCode, Intent data) {
// Check that the result was from the autocomplete widget.
        if (requestCode == TextViewTool.REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(activity, data);

                LatLng latLong = place.getLatLng();

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(19f).tilt(70).build();

                PermissionTool.checkLocationPermission(activity);

                mapTool.getGoogleMap().setMyLocationEnabled(true);
                mapTool.getGoogleMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(activity, data);
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }

}