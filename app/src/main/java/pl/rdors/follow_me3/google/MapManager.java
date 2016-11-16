package pl.rdors.follow_me3.google;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.state.map.Map;
import pl.rdors.follow_me3.utils.AppUtils;
import pl.rdors.follow_me3.view.ViewElements;

/**
 * Created by rdors on 2016-11-02.
 */

public class MapManager implements OnMapReadyCallback {

    public LatLng latLngCenter;

    private GoogleMap googleMap;

    private TestActivity activity;
    private ViewElements viewElements;

    public static String TAG = "MapManager";

    public MapManager(TestActivity activity, ViewElements viewElements) {
        this.activity = activity;
        this.viewElements = viewElements;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style));
        if (success) {
            Log.d(TAG, "Map style success!");
        }

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
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        this.googleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                activity.getApplicationState().animateWhenMapMoveStarted();
            }
        });

        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Location location = createLocation(googleMap.getCameraPosition());
                latLngCenter = new LatLng(location.getLatitude(), location.getLongitude());
                activity.getApplicationState().animateWhenMapIdle();
            }
        });

        activity.setApplicationState(new Map(activity, this, viewElements));
        activity.getApplicationState().init();

    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }
//
//    private boolean isCorrectLocation(Location location) {
//        return location != null &&
//                (location.getLongitude() != 0 || location.getLatitude() != 0);
//    }

    private Location createLocation(CameraPosition cameraPosition) {
        LatLng latLng = cameraPosition.target;
        Location location = new Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

}
