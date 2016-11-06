package pl.rdors.follow_me3.view;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;
import java.util.List;

import pl.rdors.follow_me3.Map;
import pl.rdors.follow_me3.MeetingMap;
import pl.rdors.follow_me3.MyCustomAdapter;
import pl.rdors.follow_me3.NewMeeting;
import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.model.User;
import pl.rdors.follow_me3.utils.AppUtils;

/**
 * Created by rdors on 2016-11-02.
 */

public class ViewElementsManager {

    private TestActivity activity;
    private ViewElements viewElements;

    public ViewElementsManager(TestActivity activity, ViewElements viewElements) {
        this.activity = activity;
        this.viewElements = viewElements;

        displayListView();

        viewElements.toolbarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();
            }
        });

        viewElements.buttonCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButtonCheckMark();
            }
        });

        viewElements.buttonNewMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateButtonNewMeeting();
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

    private void animateButtonNewMeeting() {
        activity.setApplicationState(new MeetingMap(activity, viewElements));
        activity.getApplicationState().init();
    }

    public void animateButtonCheckMark() {
        activity.setApplicationState(new NewMeeting(activity, viewElements));
        activity.getApplicationState().init();
    }

    public void animateWhenNewMeetingHide() {
        activity.setApplicationState(new Map(activity, viewElements));
        activity.getApplicationState().init();
    }

    public void handleLocation(String location) {
        viewElements.locationAddress.setText(location != null ? location : "Address not found");
    }

    public boolean isNewMeetingContainerVisible() {
        return viewElements.newMeetingContainer.getVisibility() == View.VISIBLE;
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
        viewElements.meetingContactsListView.setAdapter(dataAdapter);


        viewElements.meetingContactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
