package pl.rdors.follow_me3.rest.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rdors on 2016-10-21.
 */
public class Meeting {

    private Long id;
    private String name;
    private String lastUpdate;
    private boolean active;
    private List<MeetingPlace> meetingPlaces = new ArrayList<>();
    private List<MeetingUser> meetingUsers = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<MeetingPlace> getMeetingPlaces() {
        return meetingPlaces;
    }

    public void setMeetingPlaces(List<MeetingPlace> meetingPlaces) {
        this.meetingPlaces = meetingPlaces;
    }

    public List<MeetingUser> getMeetingUsers() {
        return meetingUsers;
    }

    public void setMeetingUsers(List<MeetingUser> meetingUsers) {
        this.meetingUsers = meetingUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meeting meeting = (Meeting) o;

        return id != null ? id.equals(meeting.id) : meeting.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
