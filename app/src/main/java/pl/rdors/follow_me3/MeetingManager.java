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
import java.util.Iterator;
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

    public static List<MeetingMarker> meetings;
    public static List<MeetingUserPolyline> meetingUserPolylines;

    public static List<MeetingMarker> getMeetings() {
        if (meetings == null) {
            meetings = new ArrayList<>();
        }
        return meetings;
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

}
