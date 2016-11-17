package pl.rdors.follow_me3.google;

import com.google.android.gms.maps.model.Marker;

import pl.rdors.follow_me3.rest.model.User;

/**
 * Created by rdors on 2016-11-17.
 */
public class UserMarker {

    private User user;
    private Marker marker;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
