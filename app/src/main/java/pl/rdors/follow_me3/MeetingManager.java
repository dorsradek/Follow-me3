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
import pl.rdors.follow_me3.google.MeetingMarker;
import pl.rdors.follow_me3.google.MeetingUserPolyline;
import pl.rdors.follow_me3.google.UserMarker;
import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.rest.model.MeetingUser;
import pl.rdors.follow_me3.rest.model.User;

/**
 * Created by rdors on 2016-11-12.
 */

public class MeetingManager {

    private static final String TAG = "MeetingManager";

    private static List<MeetingMarker> meetings;
    private static List<UserMarker> users;
    private static List<MeetingUserPolyline> meetingUserPolylines;

    public static List<MeetingMarker> getMeetings() {
        if (meetings == null) {
            meetings = new ArrayList<>();
        }
        return meetings;
    }

    public static List<UserMarker> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    public static List<MeetingUserPolyline> getMeetingUserPolylines() {
        if (meetingUserPolylines == null) {
            meetingUserPolylines = new ArrayList<>();
        }
        return meetingUserPolylines;
    }

    public static MeetingMarker addMeeting(Meeting meeting) {
        //TODO: when conatins check if has to add/remove users from meeting
        MeetingMarker meetingMarker = new MeetingMarker();
        if (meeting.getPlace() != null) {
            if (!meetingsContains(meeting)) {
                meetingMarker.setValid(true);
                meetingMarker.setMeeting(meeting);
                getMeetings().add(meetingMarker);

                for (MeetingUser meetingUser : meeting.getMeetingUsers()) {
                    MeetingUserPolyline meetingUserPolyline = new MeetingUserPolyline();
                    meetingUserPolyline.setMeetingMarker(meetingMarker);
                    UserMarker userMarker = new UserMarker();
                    userMarker.setUser(meetingUser.getUser());
                    meetingUserPolyline.setUserMarker(userMarker);
                    getMeetingUserPolylines().add(meetingUserPolyline);
                }
            } else {
                meetingMarker = meetingsGet(meeting);
            }
        }
        return meetingMarker;
    }

    public static void addMeeting(Meeting meeting, MapManager mapManager) {
        MeetingMarker meetingMarker = addMeeting(meeting);
        if (meetingMarker.getMarker() == null && meetingMarker.isValid()) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(meeting.getPlace().getX(), meeting.getPlace().getY()))
                    .title(meeting.getName());
            Marker marker = mapManager.getGoogleMap().addMarker(markerOptions);
            meetingMarker.setMarker(marker);
        }
    }

    public static void addUser(User user, MapManager mapManager) {
        if (usersContains(user)) {
            UserMarker userMarker = usersGet(user);
            Marker marker = userMarker.getMarker();
            userMarker.getUser().setLastUpdate(user.getLastUpdate());
            if (userMarker.getUser().getX() != user.getX() || userMarker.getUser().getY() != user.getY()) {
                userMarker.getUser().setX(user.getX());
                userMarker.getUser().setY(user.getY());
                animateMarker(marker, new LatLng(user.getX(), user.getY()), false, mapManager);
            }
        } else {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(user.getX(), user.getY()))
                    .title(user.getUsername());
            Marker marker = mapManager.getGoogleMap().addMarker(markerOptions);
            UserMarker userMarker = new UserMarker();
            userMarker.setUser(user);
            userMarker.setMarker(marker);
            getUsers().add(userMarker);

            for (MeetingUserPolyline meetingUserPolyline : getMeetingUserPolylines()) {
                if (meetingUserPolyline.getUserMarker().getUser().equals(user)) {
                    meetingUserPolyline.setUserMarker(userMarker);
                }
            }
        }

        for (MeetingUserPolyline meetingUserPolyline : getMeetingUserPolylines()) {
            if (meetingUserPolyline.getUserMarker().getUser().equals(user)) {
                Marker meetingMarker = meetingUserPolyline.getMeetingMarker().getMarker();
                Marker userMarker = meetingUserPolyline.getUserMarker().getMarker();
                if (meetingMarker != null && userMarker != null) {
                    createDirection(mapManager, meetingUserPolyline);
                }
            }
        }
    }

    private static boolean meetingsContains(Meeting meeting) {
        for (MeetingMarker meetingMarker : getMeetings()) {
            if (meetingMarker.getMeeting().equals(meeting)) {
                return true;

            }
        }
        return false;
    }

    private static MeetingMarker meetingsGet(Meeting meeting) {
        for (MeetingMarker meetingMarker : getMeetings()) {
            if (meetingMarker.getMeeting().equals(meeting)) {
                return meetingMarker;
            }
        }
        return null;
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
        Meeting meeting = meetingUserPolyline.getMeetingMarker().getMeeting();
        User user = meetingUserPolyline.getUserMarker().getUser();
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
                            PolylineOptions polylineOptions = DirectionConverter
                                    .createPolyline(mapManager.getActivity(), directionPositionList, 5, Color.RED).visible(false);
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
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

}
