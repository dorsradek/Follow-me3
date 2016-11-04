package pl.rdors.follow_me3.view;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;
import java.util.List;

import pl.rdors.follow_me3.MyCustomAdapter;
import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.model.User;
import pl.rdors.follow_me3.utils.AppUtils;

/**
 * Created by rdors on 2016-11-02.
 */

public class ViewElementsManager {

    public static final int ANIMATION_TIME = 500;

    public TextView locationMarkerText;
    public TextView locationAddress;
    public ImageButton buttonNewMeeting;
    public ImageButton buttonCheckMark;
    public LinearLayout toolbarContainer;
    public LinearLayout newMeetingContainer;
    public LinearLayout locationMarkerContainer;
    public ListView meetingContactsListView;

    private TestActivity activity;
    public IMapMovable mapMovable;

    private State state = State.MAP;

    enum State {
        MAP, NEW_MEETING_MAP, NEW_MEETING_CONTAINER
    }

    public ViewElementsManager(TestActivity activity, View view) {
        this.activity = activity;
        locationMarkerText = (TextView) view.findViewById(R.id.locationMarkertext);
        locationAddress = (TextView) view.findViewById(R.id.Address);

        buttonNewMeeting = (ImageButton) view.findViewById(R.id.button_new_meeting);
        buttonNewMeeting.setTranslationY(AppUtils.getHeightPx(activity));

        buttonCheckMark = (ImageButton) view.findViewById(R.id.button_check_mark);
        buttonCheckMark.setTranslationY(AppUtils.getHeightPx(activity));

        toolbarContainer = (LinearLayout) view.findViewById(R.id.container_toolbar);
        toolbarContainer.setTranslationY(-AppUtils.getHeightPx(activity));

        newMeetingContainer = (LinearLayout) view.findViewById(R.id.container_new_meeting);
        newMeetingContainer.setTranslationY(AppUtils.getHeightPx(activity));

        locationMarkerContainer = (LinearLayout) view.findViewById(R.id.container_location_marker);
        locationMarkerContainer.setVisibility(View.INVISIBLE);

        meetingContactsListView = (ListView) view.findViewById(R.id.list_meeting_contacts);
        displayListView();

        toolbarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();
            }
        });

        buttonCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButtonCheckMark();
            }
        });

        buttonNewMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButtonNewMeeting();
            }
        });

        mapMovable = new MapMove(this);
    }


    private void openAutocompleteActivity() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(activity);
            activity.startActivityForResult(intent, AppUtils.LocationConstants.REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.getConnectionStatusCode());
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void animateButtonNewMeeting() {
        //hide
        buttonNewMeeting.animate()
                .translationY(buttonNewMeeting.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(ANIMATION_TIME);
        buttonNewMeeting.setVisibility(View.INVISIBLE);

        //show
        buttonCheckMark.setVisibility(View.VISIBLE);
        buttonCheckMark.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);
        state = State.NEW_MEETING_MAP;
        mapMovable = new MeetingMapMove(this);
        locationMarkerContainer.setVisibility(View.VISIBLE);
    }

    public void animateButtonCheckMark() {
        //hide
        buttonCheckMark.animate()
                .translationY(buttonCheckMark.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(ANIMATION_TIME);
        buttonCheckMark.setVisibility(View.INVISIBLE);

        //show
        newMeetingContainer.setVisibility(View.VISIBLE);
        newMeetingContainer.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        toolbarContainer.setEnabled(false);
        activity.enableFragment(false);
        state = State.NEW_MEETING_CONTAINER;
        mapMovable = new MeetingContainerMapMove(this);
        locationMarkerContainer.setVisibility(View.INVISIBLE);
    }

    public void animateWhenNewMeetingHide() {
        buttonNewMeeting.setVisibility(View.VISIBLE);
        buttonNewMeeting.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        newMeetingContainer.animate()
                .translationY(newMeetingContainer.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(ANIMATION_TIME);
        newMeetingContainer.setVisibility(View.INVISIBLE);
        locationMarkerContainer.setVisibility(View.INVISIBLE);

        toolbarContainer.setEnabled(true);
        activity.enableFragment(true);
        state = State.MAP;
    }

    public void handleLocation(String location) {
        locationAddress.setText(location != null ? location : "Address not found");
    }

    public boolean isNewMeetingContainerVisible() {
        return newMeetingContainer.getVisibility() == View.VISIBLE;
    }

    MyCustomAdapter dataAdapter = null;

    private void displayListView() {

        //Array list of countries
        List<User> countryList = new ArrayList<>();
        User country = new User("AFG");
        countryList.add(country);
        country = new User("ALB");
        countryList.add(country);
        country = new User("DZA");
        countryList.add(country);
        country = new User("ASM");
        countryList.add(country);
        country = new User("AND");
        countryList.add(country);
        country = new User("AGO");
        countryList.add(country);
        country = new User("DZA");
        countryList.add(country);
        country = new User("ASM");
        countryList.add(country);
        country = new User("AND");
        countryList.add(country);
        country = new User("AGO");
        countryList.add(country);
        country = new User("AIA");
        countryList.add(country);
        country = new User("DZA");
        countryList.add(country);
        country = new User("ASM");
        countryList.add(country);
        country = new User("AND");
        countryList.add(country);
        country = new User("AGO");
        countryList.add(country);
        country = new User("AIA");
        countryList.add(country);

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(activity, R.layout.list_item, countryList);
        // Assign adapter to ListView
        meetingContactsListView.setAdapter(dataAdapter);


        meetingContactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                User country = (User) parent.getItemAtPosition(position);
                Toast.makeText(activity,
                        "Clicked on Row: " + country.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

}
