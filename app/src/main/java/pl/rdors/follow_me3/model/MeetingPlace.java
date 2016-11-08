package pl.rdors.follow_me3.model;

/**
 * Created by rdors on 2016-10-25.
 */
public class MeetingPlace {

    private boolean owner;
    private Meeting meeting;
    private Place place;

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}
