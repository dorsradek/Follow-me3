package pl.rdors.follow_me3.state.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import pl.rdors.follow_me3.LocationProvider;
import pl.rdors.follow_me3.MeetingManager;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.fragment.MapFragment;
import pl.rdors.follow_me3.google.MapManager;
import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.rest.model.Place;
import pl.rdors.follow_me3.utils.AppUtils;
import pl.rdors.follow_me3.view.ViewElements;

import static pl.rdors.follow_me3.view.ViewElements.ANIMATION_TIME;

/**
 * Created by rdors on 2016-11-06.
 */

public class Map extends MapState {

    public static String TAG = "Map";

    public Map(TestActivity activity, MapManager mapManager, ViewElements viewElements) {
        super(activity, mapManager, viewElements);
    }

    @Override
    public void init() {
//        SharedPreferences prefs = activity.getSharedPreferences("follow-me", Context.MODE_PRIVATE);
//        String token = prefs.getString("token", "");
//        Call<List<Meeting>> call = activity.getMeetingService().findAll(token);
//        call.enqueue(new Callback<List<Meeting>>() {
//            @Override
//            public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
//                if (response.isSuccessful()) {
//                    System.out.println(response.body());
//                    mapManager.focusOnMeetings(response.body());
//                } else {
//                    // error response, no access to resource?
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Meeting>> call, Throwable t) {
//                // something went completely south (like no internet connection)
//                //Log.d("Error", t.getMessage());
//            }
//        });

        viewElements.buttonNewMeeting.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.buttonNewMeeting.setVisibility(View.INVISIBLE);
        viewElements.buttonCheckMark.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.buttonCheckMark.setVisibility(View.INVISIBLE);
        viewElements.containerLocationToolbar.setTranslationY(-AppUtils.getHeightPx(activity));
        viewElements.containerLocationToolbar.setVisibility(View.INVISIBLE);
        viewElements.containerNewMeeting.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.containerNewMeeting.setVisibility(View.INVISIBLE);
        viewElements.locationMarkerContainer.setVisibility(View.INVISIBLE);

        viewElements.buttonNewMeeting.setVisibility(View.VISIBLE);
        viewElements.buttonNewMeeting.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        enable(true);

        focusOnMeetings();
    }

    @Override
    public void animateWhenMapMoveStarted() {

        viewElements.buttonNewMeeting.animate()
                .translationY(viewElements.buttonNewMeeting.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(ANIMATION_TIME);
        viewElements.buttonNewMeeting.setVisibility(View.INVISIBLE);
    }

    @Override
    public void animateWhenMapIdle() {

        viewElements.buttonNewMeeting.setVisibility(View.VISIBLE);
        viewElements.buttonNewMeeting.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);
    }

    @Override
    public boolean canBack() {
        return false;
    }

    @Override
    public void back() {

    }

    @Override
    public void buttonCheckMarkOnClick() {

    }

    public void focusOnMeetings() {
        mapManager.getGoogleMap().clear();
        LatLngBounds.Builder bld = new LatLngBounds.Builder();
        Location lastLocation = LocationProvider.getInstance().getCurrentLocation();
        if (isCorrectLocation(lastLocation)) {
            bld.include(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
        }
        for (Meeting meeting : MeetingManager.getMeetings()) {
            Place place = meeting.getPlace();
            if (place != null) {
                Location location = new Location("");
                location.setLatitude(place.getX());
                location.setLongitude(place.getY());

                bld.include(new LatLng(location.getLatitude(), location.getLongitude()));

                MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(location.getLatitude(), location.getLongitude()))
                        .title(meeting.getName());
                mapManager.getGoogleMap().addMarker(marker);
            }
        }
        try {
            LatLngBounds bounds = bld.build();

            //TODO: padding deppends on location marker size
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 30);
            mapManager.getGoogleMap().animateCamera(cameraUpdate);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }

        if (activity.getFragment() instanceof pl.rdors.follow_me3.fragment.MapFragment) {
            ((MapFragment) activity.getFragment()).progressDialog.dismiss();
        }
    }

    private boolean isCorrectLocation(Location location) {
        return location != null &&
                (location.getLongitude() != 0 || location.getLatitude() != 0);
    }

}
