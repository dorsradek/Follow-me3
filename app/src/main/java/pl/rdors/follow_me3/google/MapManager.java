package pl.rdors.follow_me3.google;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import pl.rdors.follow_me3.MeetingManager;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.fragment.MapFragment;
import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.rest.model.MeetingPlace;
import pl.rdors.follow_me3.rest.model.Place;
import pl.rdors.follow_me3.state.map.Map;
import pl.rdors.follow_me3.utils.AppUtils;
import pl.rdors.follow_me3.view.ViewElements;

/**
 * Created by rdors on 2016-11-02.
 */

public class MapManager implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public LatLng latLngCenter;

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;

    private TestActivity activity;
    private ViewElements viewElements;

    public static String TAG = "MapManager";

    public MapManager(TestActivity activity, ViewElements viewElements) {
        this.activity = activity;
        this.viewElements = viewElements;

        if (checkPlayServices()) {
            AppUtils.checkLocationEnabled(activity);
            buildGoogleApiClient();
        } else {
            Toast.makeText(activity, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        activity.setApplicationState(new Map(activity, this, viewElements));
        activity.getApplicationState().init();

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AppUtils.requestLocationPermission(activity);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        this.googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                activity.getApplicationState().animateWhenMapMoveStarted();
            }
        });

        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                activity.getApplicationState().animateWhenMapIdle();
            }
        });

        focusOnMeetings();
    }


//    public void changeLocationOnMap(Location location) {
//        if (googleMap != null) {
//            latLngCenter = new LatLng(location.getLatitude(), location.getLongitude());
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLngCenter).zoom(17f).build();
//
//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//            intentServiceTool.startIntentService(location);
//        } else {
//            Toast.makeText(activity, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
//        }
//
//    }

    public void focusOnMeetings() {
        googleMap.clear();
        LatLngBounds.Builder bld = new LatLngBounds.Builder();
        Location lastLocation = getMyLocation();
        if (isCorrectLocation(lastLocation)) {
            bld.include(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
        }
        for (Meeting meeting : MeetingManager.getMeetings()) {
            for (MeetingPlace meetingPlace : meeting.getMeetingPlaces()) {
                Place place = meetingPlace.getPlace();

                Location location = new Location("");
                location.setLatitude(place.getX());
                location.setLongitude(place.getY());

                bld.include(new LatLng(location.getLatitude(), location.getLongitude()));

                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title(meeting.getName());
                googleMap.addMarker(marker);
            }
        }
        try {
            LatLngBounds bounds = bld.build();

            //TODO: padding deppends on location marker size
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 30);
            googleMap.animateCamera(cameraUpdate);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        if (activity.getFragment() instanceof pl.rdors.follow_me3.fragment.MapFragment) {
            ((MapFragment) activity.getFragment()).progressDialog.dismiss();
        }
    }

    private Location getMyLocation() {
        //TODO: get last location from shared preferences??
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AppUtils.requestLocationPermission(activity);
            Location location = new Location("");
            location.setLongitude(0);
            location.setLatitude(0);
            return location;
        }
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
    }

    private synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(activity);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(activity, result, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AppUtils.requestLocationPermission(activity);
            return;
        }
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    private boolean isCorrectLocation(Location location) {
        return location != null &&
                (location.getLongitude() != 0 || location.getLatitude() != 0);
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }
}
