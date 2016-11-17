package pl.rdors.follow_me3;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import pl.rdors.follow_me3.utils.AppUtils;

public class LocationProvider {

    private static LocationProvider instance = null;
    private static Context context;

    public static final int ONE_MINUTE = 1000 * 60;
    public static final int FIVE_MINUTES = ONE_MINUTE * 5;
    public static final int asd_MINUTES = 1000 * 10;

    private static Location currentLocation;
    private GoogleApiClient googleApiClient;

    private LocationProvider() {
    }

    public static LocationProvider getInstance() {
        if (instance == null) {
            instance = new LocationProvider();
        }

        return instance;
    }

    public void configureIfNeeded(Context ctx) {
        if (context == null) {
            context = ctx;
            configureLocationUpdates();
        }
    }

    private void configureLocationUpdates() {
        if (checkPlayServices()) {
            AppUtils.checkLocationEnabled(context);
            buildGoogleApiClient();
        } else {
            Toast.makeText(context, "Location not supported in this device", Toast.LENGTH_SHORT).show();
        }

    }

    private static LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(FIVE_MINUTES);
        return locationRequest;
    }

    private static void startLocationUpdates(GoogleApiClient client, LocationRequest request) {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (context instanceof AppCompatActivity) {
                AppUtils.requestLocationPermission((AppCompatActivity) context);
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
            }
        });
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if (result != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    private void buildGoogleApiClient() {
        final LocationRequest locationRequest = createLocationRequest();
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                startLocationUpdates(googleApiClient, locationRequest);
            }

            @Override
            public void onConnectionSuspended(int i) {
            }
        });
        googleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
            }
        });

        googleApiClient.connect();
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

}