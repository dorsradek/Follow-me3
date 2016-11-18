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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserMarker that = (UserMarker) o;

        return user != null ? user.equals(that.user) : that.user == null;

    }

    @Override
    public int hashCode() {
        return user != null ? user.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserMarker{" +
                "user=" + user +
                ", marker=" + marker +
                '}';
    }
}
