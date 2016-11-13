package pl.rdors.follow_me3;

import java.util.HashSet;
import java.util.Set;

import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.rest.model.User;

/**
 * Created by rdors on 2016-11-12.
 */

public class MeetingManager {

    private static Set<Meeting> meetings;

    private static Set<User> facebookFriends;

    public static Set<Meeting> getMeetings() {
        if (meetings == null) {
            meetings = new HashSet<>();
        }
        return meetings;
    }

    public static Set<User> getFacebookFriends() {
        if (facebookFriends == null) {
            facebookFriends = new HashSet<>();
        }
        return facebookFriends;
    }

}
