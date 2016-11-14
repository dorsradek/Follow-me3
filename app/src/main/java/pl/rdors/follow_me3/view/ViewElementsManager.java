package pl.rdors.follow_me3.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pl.rdors.follow_me3.MyCustomAdapter;
import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.fragment.MapFragment;
import pl.rdors.follow_me3.rest.ServiceGenerator;
import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.rest.model.Place;
import pl.rdors.follow_me3.rest.model.User;
import pl.rdors.follow_me3.rest.service.MeetingService;
import pl.rdors.follow_me3.state.map.MeetingMap;
import pl.rdors.follow_me3.state.map.NewMeeting;
import pl.rdors.follow_me3.utils.AppUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        viewElements.iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAddressElementOnClick();
            }
        });

        viewElements.textAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAddressElementOnClick();
            }
        });

        viewElements.buttonCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonCheckMarkOnClick();
            }
        });

        viewElements.buttonNewMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonNewMeetingOnClick();
            }
        });
    }


    private void searchAddressElementOnClick() {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(activity);
            activity.startActivityForResult(intent, AppUtils.LocationConstants.REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.getConnectionStatusCode());
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " + GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void buttonNewMeetingOnClick() {
        if (activity.getFragment() != null
                && activity.getFragment() instanceof MapFragment) {
            activity.setApplicationState(new MeetingMap(activity, ((MapFragment) activity.getFragment()).getMapManager(), viewElements));
            activity.getApplicationState().init();
        }
    }

    private void buttonCheckMarkOnClick() {
        if (activity.getFragment() != null
                && activity.getFragment() instanceof MapFragment) {
            activity.setApplicationState(new NewMeeting(activity, ((MapFragment) activity.getFragment()).getMapManager(), viewElements));
            activity.getApplicationState().init();

            LatLng latLng = ((MapFragment) activity.getFragment()).getMapManager().latLngCenter;
            Meeting m = new Meeting();
            m.setName("ASD");
            Place place = new Place();
            place.setName("aww");
            place.setX(latLng.latitude);
            place.setY(latLng.longitude);
            m.setPlace(place);

            //TODO: repair adding new meeting

            SharedPreferences prefs = activity.getSharedPreferences("follow-me", Context.MODE_PRIVATE);
            String token = prefs.getString("token", "");
            Call<ResponseBody> call = ServiceGenerator.createService(MeetingService.class).create(m, token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    System.out.println(response.message());
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    System.out.println(t.getMessage());
                }
            });
        }
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
                        "Clicked on Row: " + country.getUsername(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

}
