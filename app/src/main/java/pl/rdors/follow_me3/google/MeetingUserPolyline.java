package pl.rdors.follow_me3.google;

import com.google.android.gms.maps.model.Polyline;

/**
 * Created by rdors on 2016-11-17.
 */

public class MeetingUserPolyline {

    private MeetingMarker meetingMarker;
    private UserMarker userMarker;
    private Polyline polyline;

    public MeetingMarker getMeetingMarker() {
        return meetingMarker;
    }

    public void setMeetingMarker(MeetingMarker meetingMarker) {
        this.meetingMarker = meetingMarker;
    }

    public UserMarker getUserMarker() {
        return userMarker;
    }

    public void setUserMarker(UserMarker userMarker) {
        this.userMarker = userMarker;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }
}
