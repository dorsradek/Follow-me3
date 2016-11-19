package pl.rdors.follow_me3.google;

import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rdors on 2016-11-17.
 */

public class MeetingUserPolyline {

    private MeetingMarker meetingMarker;
    private UserMarker userMarker;
    private List<Polyline> polylines;

    private String duration;

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

    public List<Polyline> getPolylines() {
        if (polylines == null) {
            polylines = new ArrayList<>();
        }
        return polylines;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void polylinesSetVisible(boolean visible) {
        for (Polyline polyline : getPolylines()) {
            polyline.setVisible(visible);
        }
    }

    public boolean polylinesIsVisible() {
        for (Polyline polyline : getPolylines()) {
            if (polyline.isVisible()) {
                return true;
            }
        }
        return false;
    }

    public void polylinesRemove() {
        for (Polyline polyline : getPolylines()) {
            polyline.remove();
        }
    }

}
