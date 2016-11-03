package pl.rdors.follow_me3.view;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.List;

import pl.rdors.follow_me3.MyCustomAdapter;
import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.fragment.MapFragment;
import pl.rdors.follow_me3.model.User;
import pl.rdors.follow_me3.utils.AppUtils;

/**
 * Created by rdors on 2016-11-02.
 */

public class ViewElementsManager {

    private static final int ANIMATION_TIME = 500;

    private TextView locationMarkerText;
    private TextView locationAddress;
    private Button buttonNewMeeting;
    private LinearLayout toolbarContainer;
    private LinearLayout newMeetingContainer;
    private ListView meetingContactsListView;

    private TestActivity activity;

    public ViewElementsManager(TestActivity activity, View view) {
        this.activity = activity;
        locationMarkerText = (TextView) view.findViewById(R.id.locationMarkertext);
        locationAddress = (TextView) view.findViewById(R.id.Address);

        buttonNewMeeting = (Button) view.findViewById(R.id.button_new_meeting);
        buttonNewMeeting.setTranslationY(AppUtils.getHeightPx(activity));

        toolbarContainer = (LinearLayout) view.findViewById(R.id.container_toolbar);
        toolbarContainer.setTranslationY(-AppUtils.getHeightPx(activity));

        newMeetingContainer = (LinearLayout) view.findViewById(R.id.container_new_meeting);
        newMeetingContainer.setTranslationY(AppUtils.getHeightPx(activity));

        meetingContactsListView = (ListView) view.findViewById(R.id.list_meeting_contacts);
        displayListView();

        toolbarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();
            }
        });

        buttonNewMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateWhenNewMeetingShow();
            }
        });
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

    public void animateWhenMapMoveStarted() {
        toolbarContainer.animate()
                .translationY(-toolbarContainer.getHeight() - 20)
                .alpha(0.0f)
                .setDuration(ANIMATION_TIME);
        toolbarContainer.setVisibility(View.INVISIBLE);

        buttonNewMeeting.animate()
                .translationY(buttonNewMeeting.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(ANIMATION_TIME);
        buttonNewMeeting.setVisibility(View.INVISIBLE);
    }

    public void animateWhenMapIdle() {
        if (!isNewMeetingContainerVisible()) {
            buttonNewMeeting.setVisibility(View.VISIBLE);
            buttonNewMeeting.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setDuration(ANIMATION_TIME);

            toolbarContainer.setVisibility(View.VISIBLE);
            toolbarContainer.animate()
                    .translationY(0)
                    .alpha(1.0f)
                    .setDuration(ANIMATION_TIME);
        }
    }

    public void animateWhenNewMeetingShow() {
        buttonNewMeeting.animate()
                .translationY(buttonNewMeeting.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(ANIMATION_TIME);
        buttonNewMeeting.setVisibility(View.INVISIBLE);

        newMeetingContainer.setVisibility(View.VISIBLE);
        newMeetingContainer.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        toolbarContainer.setEnabled(false);
        activity.enableFragment(false);
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

        toolbarContainer.setEnabled(true);
        activity.enableFragment(true);
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
