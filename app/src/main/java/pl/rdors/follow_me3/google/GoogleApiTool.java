package pl.rdors.follow_me3.google;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.utils.AppUtils;

/**
 * Created by rdors on 2016-11-02.
 */

public class GoogleApiTool implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private GoogleApiClient googleApiClient;

    private TestActivity activity;
    private MapManager mapManager;

    public static String TAG = "MAP LOCATION";

    public GoogleApiTool(final TestActivity activity, MapManager mapManager) {
        this.activity = activity;
        this.mapManager = mapManager;

        if (checkPlayServices()) {
            AppUtils.checkLocationEnabled(activity);
            buildGoogleApiClient();
        } else {
            Toast.makeText(activity, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }
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
        AppUtils.checkLocationPermission(activity);

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mLastLocation != null) {
            mapManager.changeLocationOnMap(mLastLocation);
        } else {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
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
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

}
