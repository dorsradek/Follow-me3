package pl.rdors.follow_me3.google;

import com.google.android.gms.maps.model.Marker;

import pl.rdors.follow_me3.rest.model.Meeting;

/**
 * Created by rdors on 2016-11-17.
 */

public class MeetingMarker {

    private boolean valid;
    private Meeting meeting;
    private Marker marker;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
