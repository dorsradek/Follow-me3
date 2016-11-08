package pl.rdors.follow_me3.google;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.intentservice.IntentServiceTool;
import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.rest.model.MeetingPlace;
import pl.rdors.follow_me3.rest.model.Place;
import pl.rdors.follow_me3.state.map.Map;
import pl.rdors.follow_me3.utils.AppUtils;
import pl.rdors.follow_me3.view.ViewElements;
import pl.rdors.follow_me3.view.ViewElementsManager;

/**
 * Created by rdors on 2016-11-02.
 */

public class MapManager implements OnMapReadyCallback {

    private GoogleMap googleMap;

    private TestActivity activity;
    private IntentServiceTool intentServiceTool;
    private ViewElementsManager viewElementsManager;
    private ViewElements viewElements;

    public MapManager(TestActivity activity, IntentServiceTool intentServiceTool, ViewElementsManager viewElementsManager, ViewElements viewElements) {
        this.activity = activity;
        this.intentServiceTool = intentServiceTool;
        this.viewElementsManager = viewElementsManager;
        this.viewElements = viewElements;
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
                viewElementsManager.handleLocation("");
            }
        });

        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Location location = createLocation(googleMap.getCameraPosition());
                intentServiceTool.startIntentService(location);
                activity.getApplicationState().animateWhenMapIdle();
            }
        });
    }

    @NonNull
    private Location createLocation(CameraPosition cameraPosition) {
        LatLng latLngCenter = cameraPosition.target;
        Location location = new Location("");
        location.setLatitude(latLngCenter.latitude);
        location.setLongitude(latLngCenter.longitude);
        return location;
    }

    public void changeLocationOnMap(Location location) {
        if (googleMap != null) {
            LatLng latLngCenter = new LatLng(location.getLatitude(), location.getLongitude());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLngCenter).zoom(17f).build();

            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            intentServiceTool.startIntentService(location);
        } else {
            Toast.makeText(activity, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }

    }

    public void focusOnMeetings(List<Meeting> meetings) {
        googleMap.clear();
        for (Meeting meeting : meetings) {
            for (MeetingPlace meetingPlace : meeting.getMeetingPlaces()) {
                Place place = meetingPlace.getPlace();

                Location location = new Location("");
                location.setLatitude(place.getX());
                location.setLongitude(place.getY());

                changeLocationOnMap(location);

                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title(meeting.getName());
                googleMap.addMarker(marker);
            }
        }
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

}
