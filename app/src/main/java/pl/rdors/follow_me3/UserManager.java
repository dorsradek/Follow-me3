package pl.rdors.follow_me3;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import pl.rdors.follow_me3.google.MapManager;
import pl.rdors.follow_me3.google.MeetingUserPolyline;
import pl.rdors.follow_me3.google.UserMarker;
import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.rest.model.User;

/**
 * Created by rdors on 2016-11-18.
 */

public class UserManager {

    private static final String TAG = "UserManager";

    public static List<UserMarker> users;

    public static List<UserMarker> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }


    public static void addOrUpdateUsers(List<User> users, MapManager mapManager) {
        for (User user : users) {
            addOrUpdateUser(user, mapManager);
        }

        List<UserMarker> usersToRemove = new ArrayList<>();
        for (UserMarker userMarker : getUsers()) {
            if (!users.contains(userMarker.getUser())) {
                usersToRemove.add(userMarker);
            }
        }

        List<MeetingUserPolyline> meetingUserPolylinesToRemove = new ArrayList<>();
        for (MeetingUserPolyline meetingUserPolyline : MeetingManager.getMeetingUserPolylines()) {
            if (usersToRemove.contains(meetingUserPolyline.getUserMarker())) {
                if (meetingUserPolyline.getPolyline() != null) {
                    meetingUserPolyline.getPolyline().remove();
                }
                if (meetingUserPolyline.getUserMarker().getMarker() != null) {
                    meetingUserPolyline.getUserMarker().getMarker().remove();
                }
                meetingUserPolylinesToRemove.add(meetingUserPolyline);
            }
        }

        getUsers().removeAll(usersToRemove);
        MeetingManager.getMeetingUserPolylines().removeAll(meetingUserPolylinesToRemove);
    }

    public static void addOrUpdateUser(User user, MapManager mapManager) {
        UserMarker userMarker;
        if (usersContains(user)) {
            userMarker = usersGet(user);
            if (userMarker.getMarker() == null) {

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(user.getX(), user.getY()))
                        .title(user.getUsername()).visible(false);
                Marker marker = mapManager.getGoogleMap().addMarker(markerOptions);
                userMarker.setMarker(marker);
            }
        } else {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(user.getX(), user.getY()))
                    .title(user.getUsername()).visible(false);
            Marker marker = mapManager.getGoogleMap().addMarker(markerOptions);
            userMarker = new UserMarker();
            userMarker.setUser(user);
            userMarker.setMarker(marker);
            getUsers().add(userMarker);
        }

        for (MeetingUserPolyline meetingUserPolyline : MeetingManager.getMeetingUserPolylines()) {
            if (meetingUserPolyline.getUserMarker().getUser().equals(user)) {
                meetingUserPolyline.setUserMarker(userMarker);

                if (locationChanged(user, userMarker)) {
                    userMarker.getUser().setLastUpdate(user.getLastUpdate());
                    userMarker.getUser().setX(user.getX());
                    userMarker.getUser().setY(user.getY());
                    animateMarker(userMarker.getMarker(), new LatLng(user.getX(), user.getY()), false, mapManager);
                    createDirection(mapManager, meetingUserPolyline);
                } else if (meetingUserPolyline.getPolyline() == null) {
                    createDirection(mapManager, meetingUserPolyline);
                }
            }
        }
    }

    private static boolean locationChanged(User newUser, UserMarker oldUserMarker) {
        return oldUserMarker.getUser().getX() != newUser.getX() ||
                oldUserMarker.getUser().getY() != newUser.getY();
    }

    private static boolean usersContains(User user) {
        for (UserMarker userMarker : getUsers()) {
            if (userMarker.getUser().equals(user)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private static UserMarker usersGet(User user) {
        for (UserMarker userMarker : getUsers()) {
            if (userMarker.getUser().equals(user)) {
                return userMarker;
            }
        }
        return null;
    }

    private static void createDirection(final MapManager mapManager, final MeetingUserPolyline meetingUserPolyline) {
        final Meeting meeting = meetingUserPolyline.getMeetingMarker().getMeeting();
        final User user = meetingUserPolyline.getUserMarker().getUser();
        GoogleDirection.withServerKey(mapManager.getActivity().getString(R.string.geo_api_key))
                .from(new LatLng(user.getX(), user.getY()))
                .to(new LatLng(meeting.getPlace().getX(), meeting.getPlace().getY()))
                .transportMode(TransportMode.WALKING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if (direction.isOK()) {
                            Route route = direction.getRouteList().get(0);
                            Leg leg = route.getLegList().get(0);
                            ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                            Log.d(TAG, user.getUsername() + " " + user.getColor());
                            PolylineOptions polylineOptions = DirectionConverter
                                    .createPolyline(mapManager.getActivity(), directionPositionList, 5, Color.parseColor(user.getColor())).visible(false);
                            Polyline polyline = mapManager.getGoogleMap().addPolyline(polylineOptions);
                            if (meetingUserPolyline.getPolyline() != null) {
                                if (meetingUserPolyline.getPolyline().isVisible()) {
                                    polyline.setVisible(true);
                                }
                                meetingUserPolyline.getPolyline().remove();
                            }
                            meetingUserPolyline.setPolyline(polyline);
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

    private static void animateMarker(final Marker marker, final LatLng toPosition,
                                      final boolean hideMarker, MapManager mapManager) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mapManager.getGoogleMap().getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
}
