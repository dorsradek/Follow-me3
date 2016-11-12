package pl.rdors.follow_me3.view;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import pl.rdors.follow_me3.R;

/**
 * Created by rdors on 2016-11-06.
 */

public class ViewElements {

    public static final int ANIMATION_TIME = 500;

    public TextView locationMarkerText;
    public TextView locationAddress;
    public ImageButton buttonNewMeeting;
    public ImageButton buttonCheckMark;
    public LinearLayout toolbarContainer;
    public LinearLayout newMeetingContainer;
    public LinearLayout locationMarkerContainer;
    public ListView meetingContactsListView;

    public ViewElements(View view) {
        locationMarkerText = (TextView) view.findViewById(R.id.locationMarkertext);
        locationAddress = (TextView) view.findViewById(R.id.Address);
        buttonNewMeeting = (ImageButton) view.findViewById(R.id.button_new_meeting);
        buttonCheckMark = (ImageButton) view.findViewById(R.id.button_check_mark);
        toolbarContainer = (LinearLayout) view.findViewById(R.id.container_toolbar);
        newMeetingContainer = (LinearLayout) view.findViewById(R.id.container_new_meeting);
        locationMarkerContainer = (LinearLayout) view.findViewById(R.id.container_location_marker);
        meetingContactsListView = (ListView) view.findViewById(R.id.list_meeting_contacts);
    }

    public void handleLocation(String location) {
        locationAddress.setText(location != null ? location : "Address not found");
    }
}
