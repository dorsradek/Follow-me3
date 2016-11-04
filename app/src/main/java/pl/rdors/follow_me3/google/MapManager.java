package pl.rdors.follow_me3.google;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.intentservice.IntentServiceTool;
import pl.rdors.follow_me3.utils.AppUtils;
import pl.rdors.follow_me3.view.ViewElementsManager;

/**
 * Created by rdors on 2016-11-02.
 */

public class MapManager implements OnMapReadyCallback {

    private LatLng latLngCenter;
    private GoogleMap googleMap;

    private TestActivity activity;
    private IntentServiceTool intentServiceTool;
    private ViewElementsManager viewElementsManager;

    public MapManager(TestActivity activity, IntentServiceTool intentServiceTool, ViewElementsManager viewElementsManager) {
        this.activity = activity;
        this.intentServiceTool = intentServiceTool;
        this.viewElementsManager = viewElementsManager;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

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
                viewElementsManager.mapMovable.animateWhenMapMoveStarted();
                viewElementsManager.handleLocation("");
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
                viewElementsManager.mapMovable.animateWhenMapIdle();
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
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLngCenter).zoom(17f).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            intentServiceTool.startIntentService(location);
        } else {
            Toast.makeText(activity, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }

    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

}
