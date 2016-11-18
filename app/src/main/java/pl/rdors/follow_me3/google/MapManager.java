package pl.rdors.follow_me3.google;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;

import pl.rdors.follow_me3.MeetingManager;
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
                boolean isMeetingMarker = isMeetingMarker(marker);

                boolean hasUser = false;
                if (isMeetingMarker) {
                    for (MeetingUserPolyline meetingUserPolyline : MeetingManager.getMeetingUserPolylines()) {
                        if (meetingUserPolyline.getPolyline() != null) {
                            if (meetingUserPolyline.getMeetingMarker().getMarker().equals(marker)) {
                                meetingUserPolyline.getPolyline().setVisible(true);
                            } else {
                                meetingUserPolyline.getPolyline().setVisible(false);
                            }
                        }
                        if (meetingUserPolyline.getMeetingMarker().getMarker().equals(marker)) {
                            if (meetingUserPolyline.getMeetingMarker().getMarker() != null) {
                                meetingUserPolyline.getMeetingMarker().getMarker().setVisible(true);
                            }
                            if (meetingUserPolyline.getUserMarker().getMarker() != null) {
                                meetingUserPolyline.getUserMarker().getMarker().setVisible(true);
                                hasUser = true;
                            }
                        } else {
                            if (meetingUserPolyline.getMeetingMarker().getMarker() != null) {
                                meetingUserPolyline.getMeetingMarker().getMarker().setVisible(false);
                            }
                        }
                    }
                }

                if (isMeetingMarker && hasUser) {
                    focusOnMeetingAndUsers(marker);
                } else {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(marker.getPosition());
                    googleMap.animateCamera(cameraUpdate);
                }
                marker.showInfoWindow();
                return true;
            }
        });

        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                for (MeetingUserPolyline meetingUserPolyline : MeetingManager.getMeetingUserPolylines()) {
                    if (meetingUserPolyline.getPolyline() != null) {
                        meetingUserPolyline.getPolyline().setVisible(false);
                    }
                    if (meetingUserPolyline.getMeetingMarker().getMarker() != null) {
                        meetingUserPolyline.getMeetingMarker().getMarker().setVisible(true);
                    }
                    if (meetingUserPolyline.getUserMarker().getMarker() != null) {
                        meetingUserPolyline.getUserMarker().getMarker().setVisible(false);
                    }
                }
            }
        });

        activity.setApplicationState(new Map(activity, this, viewElements));
        activity.getApplicationState().init();

    }


    private boolean isMeetingMarker(Marker marker) {
        for (MeetingUserPolyline meetingUserPolyline : MeetingManager.getMeetingUserPolylines()) {
            if (meetingUserPolyline.getMeetingMarker().getMarker().equals(marker)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUserMarker(Marker marker) {
        for (MeetingUserPolyline meetingUserPolyline : MeetingManager.getMeetingUserPolylines()) {
            if (meetingUserPolyline.getUserMarker().getMarker().equals(marker)) {
                return true;
            }
        }
        return false;
    }

    private void focusOnMeetingAndUsers(Marker marker) {
        LatLngBounds.Builder bld = new LatLngBounds.Builder();

        for (MeetingUserPolyline meetingUserPolyline : MeetingManager.getMeetingUserPolylines()) {
            if (meetingUserPolyline.getMeetingMarker().getMarker().equals(marker) &&
                    meetingUserPolyline.getUserMarker() != null &&
                    meetingUserPolyline.getUserMarker().getMarker() != null &&
                    meetingUserPolyline.getUserMarker().getMarker().isVisible()) {
                LatLng userLatLng = new LatLng(
                        meetingUserPolyline.getUserMarker().getUser().getX(),
                        meetingUserPolyline.getUserMarker().getUser().getY());
                bld.include(userLatLng);
                LatLng meetingLatLng = new LatLng(
                        meetingUserPolyline.getMeetingMarker().getMeeting().getPlace().getX(),
                        meetingUserPolyline.getMeetingMarker().getMeeting().getPlace().getY());
                bld.include(meetingLatLng);
            }
        }

        LatLngBounds bounds = bld.build();

        //TODO: padding deppends on location marker size
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 250);
        googleMap.animateCamera(cameraUpdate);
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
