package pl.rdors.follow_me3;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.rest.model.User;

/**
 * Created by rdors on 2016-11-12.
 */

public class MeetingManager {

    private static Set<Meeting> meetings;

    private static Map<User, Marker> friends;

    private static Map<Meeting, Marker> meetingsMarkers;

    public static Set<Meeting> getMeetings() {
        if (meetings == null) {
            meetings = new HashSet<>();
        }
        return meetings;
    }

    public static Map<User, Marker> getFriends() {
        if (friends == null) {
            friends = new HashMap<>();
        }
        return friends;
    }

    public static Map<Meeting, Marker> getMeetingsMarkers() {
        if (meetingsMarkers == null) {
            meetingsMarkers = new HashMap<>();
        }
        return meetingsMarkers;
    }

}
