package pl.rdors.follow_me3.view;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pl.rdors.follow_me3.R;

/**
 * Created by rdors on 2016-11-06.
 */

public class ViewElements {

    public static final int ANIMATION_TIME = 500;

    public TextView locationMarkerText;
    public ImageButton buttonNewMeeting;
    public ImageButton buttonCheckMark;
    public LinearLayout locationMarkerContainer;
    public ListView meetingContactsListView;

    public LinearLayout containerNewMeeting;
    public TextView textNewMeetingName;

    public RelativeLayout containerLocationToolbar;
    public TextView textAddress;
    public ImageView iconMenu;
    public ImageView iconSearch;

    public ViewElements(View view) {
        locationMarkerText = (TextView) view.findViewById(R.id.locationMarkertext);
        buttonNewMeeting = (ImageButton) view.findViewById(R.id.button_new_meeting);
        buttonCheckMark = (ImageButton) view.findViewById(R.id.button_check_mark);
        locationMarkerContainer = (LinearLayout) view.findViewById(R.id.container_location_marker);
        meetingContactsListView = (ListView) view.findViewById(R.id.list_meeting_contacts);

        containerNewMeeting = (LinearLayout) view.findViewById(R.id.container_new_meeting);
        textNewMeetingName = (TextView) view.findViewById(R.id.text_new_meeting_name);

        containerLocationToolbar = (RelativeLayout) view.findViewById(R.id.container_location_toolbar);
        textAddress = (TextView) view.findViewById(R.id.text_address);
        iconMenu = (ImageView) view.findViewById(R.id.icon_menu);
        iconSearch = (ImageView) view.findViewById(R.id.icon_search);
    }

    public void handleLocation(String location) {
        textAddress.setText(location != null ? location : "Address not found");
    }
}
