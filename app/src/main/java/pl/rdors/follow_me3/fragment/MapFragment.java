package pl.rdors.follow_me3.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.google.GoogleApiTool;
import pl.rdors.follow_me3.google.MapManager;
import pl.rdors.follow_me3.intentservice.AddressResultReceiver;
import pl.rdors.follow_me3.intentservice.IntentServiceTool;
import pl.rdors.follow_me3.utils.AppUtils;
import pl.rdors.follow_me3.view.ViewElementsManager;

import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment implements IOnActivityResult, BackPressable, AbleToEnable {

    private TestActivity activity;
    private ViewElementsManager viewElementsManager;
    private MapManager mapManager;
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

        viewElementsManager = new ViewElementsManager(activity, view);
        addressResultReceiver = new AddressResultReceiver(new Handler(), viewElementsManager);
        intentServiceTool = new IntentServiceTool(addressResultReceiver, activity);
        mapManager = new MapManager(activity, intentServiceTool, viewElementsManager);
        googleApiTool = new GoogleApiTool(activity, mapManager);

        mapFragment.getMapAsync(mapManager);

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
        if (requestCode == AppUtils.LocationConstants.REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(activity, data);
                LatLng latLong = place.getLatLng();
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(17f).build();

                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    AppUtils.requestLocationPermission(activity);
                    return;
                }
                mapManager.getGoogleMap().setMyLocationEnabled(true);
                mapManager.getGoogleMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    @Override
    public boolean allowBackPress() {
        return !viewElementsManager.isNewMeetingContainerVisible();
    }

    @Override
    public void backPress() {
        viewElementsManager.animateWhenNewMeetingHide();
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    @Override
    public void enable(boolean enable) {
        mapManager.getGoogleMap().getUiSettings().setScrollGesturesEnabled(enable);
        mapManager.getGoogleMap().getUiSettings().setIndoorLevelPickerEnabled(enable);
        mapManager.getGoogleMap().getUiSettings().setZoomGesturesEnabled(enable);
    }
}