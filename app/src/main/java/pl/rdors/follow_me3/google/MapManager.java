package pl.rdors.follow_me3.google;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import pl.rdors.follow_me3.MeetingManager;
import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.rest.model.MeetingUser;
import pl.rdors.follow_me3.rest.model.User;
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

        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style));
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

        this.googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (MeetingUserPolyline meetingUserPolyline : MeetingManager.getMeetingUserPolylines()) {
                    if (meetingUserPolyline.getPolyline() != null) {
                        if (meetingUserPolyline.getMeetingMarker().getMarker().equals(marker)) {
                            meetingUserPolyline.getPolyline().setVisible(true);
                        } else {
                            meetingUserPolyline.getPolyline().setVisible(false);
                        }
                    }
                }
                return false;
            }
        });

        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                for (MeetingUserPolyline meetingUserPolyline : MeetingManager.getMeetingUserPolylines()) {
                    if (meetingUserPolyline.getPolyline() != null) {
                        meetingUserPolyline.getPolyline().setVisible(false);
                    }
                }
            }
        });

        activity.setApplicationState(new Map(activity, this, viewElements));
        activity.getApplicationState().init();

    }

    private void handleUsersInMeeting(Meeting meeting, Marker meetingMarker) {
        for (MeetingUser meetingUser : meeting.getMeetingUsers()) {
            User user = meetingUser.getUser();
            for (UserMarker item : MeetingManager.getUsers()) {
                if (item.getUser().equals(user)) {
                    Marker friendMarker = item.getMarker();
                    createDirection(meetingMarker, friendMarker);
                }
            }
        }
    }

    private void createDirection(Marker meetingMarker, Marker friendMarker) {
        String serverKey = "AIzaSyA1REipsIwyXrmR6BzG4KXGAm2Cadi5kb8";
        GoogleDirection.withServerKey(serverKey)
                .from(friendMarker.getPosition())
                .to(meetingMarker.getPosition())
                .transportMode(TransportMode.WALKING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            PolylineOptions polylineOptions = DirectionConverter
                                    .createPolyline(MapManager.this.activity, directionPositionList, 5, Color.RED);
                            Polyline polyline = googleMap.addPolyline(polylineOptions);
                        } else {
                            Log.d(TAG, direction.getErrorMessage());
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.d(TAG, t.getMessage());
                    }
                });
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    private Location createLocation(CameraPosition cameraPosition) {
        LatLng latLng = cameraPosition.target;
        Location location = new Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

    public TestActivity getActivity() {
        return activity;
    }
}
