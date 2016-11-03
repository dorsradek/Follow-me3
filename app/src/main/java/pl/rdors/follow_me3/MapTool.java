package pl.rdors.follow_me3;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.TimeUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rdors on 2016-11-02.
 */

public class MapTool implements OnMapReadyCallback {

    private LatLng latLngCenter;
    private GoogleMap googleMap;

    private TestActivity activity;
    private IntentServiceTool intentServiceTool;
    private TextViewTool textViewTool;

    public static String TAG = "MAP LOCATION";

    public MapTool(TestActivity activity, IntentServiceTool intentServiceTool, TextViewTool textViewTool) {
        this.activity = activity;
        this.intentServiceTool = intentServiceTool;
        this.textViewTool = textViewTool;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        PermissionTool.checkLocationPermission(activity);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);

        this.googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                textViewTool.getLocationAddress().setVisibility(View.INVISIBLE);
                textViewTool.getLocationAddress().setText("");
                textViewTool.getLocationMarkerText().setVisibility(View.INVISIBLE);
            }
        });

        this.googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {

            }
        });


        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Location location = createLocation(googleMap.getCameraPosition());
                intentServiceTool.startIntentService(location);
            }
        });
    }

    @NonNull
    private Location createLocation(CameraPosition cameraPosition) {
        latLngCenter = cameraPosition.target;

        Location location = new Location("");
        location.setLatitude(latLngCenter.latitude);
        location.setLongitude(latLngCenter.longitude);
        return location;
    }

    public void changeLocationOnMap(Location location) {

        if (googleMap != null) {
            latLngCenter = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLngCenter).zoom(15f).tilt(70).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            intentServiceTool.startIntentService(location);
        } else {
            Toast.makeText(activity.getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

}
