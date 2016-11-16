package pl.rdors.follow_me3.state.map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import okhttp3.ResponseBody;
import pl.rdors.follow_me3.MeetingManager;
import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.UserArrayAdapter;
import pl.rdors.follow_me3.fragment.MapFragment;
import pl.rdors.follow_me3.google.MapManager;
import pl.rdors.follow_me3.rest.ServiceGenerator;
import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.rest.model.MeetingUser;
import pl.rdors.follow_me3.rest.model.Place;
import pl.rdors.follow_me3.rest.model.User;
import pl.rdors.follow_me3.rest.service.MeetingService;
import pl.rdors.follow_me3.rest.service.UserService;
import pl.rdors.follow_me3.view.ViewElements;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pl.rdors.follow_me3.view.ViewElements.ANIMATION_TIME;

/**
 * Created by rdors on 2016-11-06.
 */

public class NewMeeting extends MapState {

    private static final String TAG = "NewMeeting";

    UserArrayAdapter dataAdapter;
    ProgressDialog progressDialog;

    public NewMeeting(TestActivity activity, MapManager mapManager, ViewElements viewElements) {
        super(activity, mapManager, viewElements);
    }

    @Override
    public void init() {

        progressDialog = new ProgressDialog(activity, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating...");
        progressDialog.setCanceledOnTouchOutside(false);

        initUsers();

        //show
        viewElements.containerNewMeeting.setVisibility(View.VISIBLE);
        viewElements.containerNewMeeting.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        //show
        viewElements.buttonCheckMark.setVisibility(View.VISIBLE);
        viewElements.buttonCheckMark.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        viewElements.containerLocationToolbar.setEnabled(false);
        viewElements.locationMarkerContainer.setVisibility(View.INVISIBLE);

        enable(false);
    }

    private void initUsers() {

        SharedPreferences prefs = activity.getSharedPreferences("follow-me", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");

        Call<List<User>> call = ServiceGenerator
                .createService(UserService.class)
                .findAll(token);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> users = response.body();
                dataAdapter = new UserArrayAdapter(activity, R.layout.list_item, users);
                viewElements.meetingContactsListView.setAdapter(dataAdapter);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    public void animateWhenMapMoveStarted() {

    }

    @Override
    public void animateWhenMapIdle() {

    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    public void back() {
        activity.setApplicationState(new MeetingMap(activity, mapManager, viewElements));
        activity.getApplicationState().init();
    }


    @Override
    public void buttonCheckMarkOnClick() {
        if (activity.getFragment() != null
                && activity.getFragment() instanceof MapFragment) {

            progressDialog.show();
            LatLng latLng = ((MapFragment) activity.getFragment()).getMapManager().latLngCenter;
            Meeting m = new Meeting();
            m.setName(viewElements.textNewMeetingName.getText().toString());
            Place place = new Place();
            place.setX(latLng.latitude);
            place.setY(latLng.longitude);
            place.setName(viewElements.textAddress.getText().toString());
            m.setPlace(place);

            final List<User> users = dataAdapter.getUsers();
            for (User user : users) {
                if (user.isSelected()) {
                    MeetingUser meetingUser = new MeetingUser();
                    meetingUser.setUser(user);
                    m.getMeetingUsers().add(meetingUser);
                }
            }

            SharedPreferences prefs = activity.getSharedPreferences("follow-me", Context.MODE_PRIVATE);
            final String token = prefs.getString("token", "");
            Call<ResponseBody> call = ServiceGenerator.createService(MeetingService.class).create(m, token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    MeetingService meetingService = ServiceGenerator.createService(MeetingService.class);
                    meetingService.findAll(token).enqueue(new Callback<List<Meeting>>() {
                        @Override
                        public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
                            MeetingManager.getMeetings().clear();
                            MeetingManager.getMeetings().addAll(response.body());

                            View view = activity.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }

                            for (User user : users) {
                                user.setSelected(false);
                            }
                            viewElements.textNewMeetingName.setText("");

                            activity.setApplicationState(new Map(activity, ((MapFragment) activity.getFragment()).getMapManager(), viewElements));
                            activity.getApplicationState().init();
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<List<Meeting>> call, Throwable t) {
                            Log.d(TAG, t.getMessage());

                            View view = activity.getCurrentFocus();
                            if (view != null) {
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                            for (User user : users) {
                                user.setSelected(false);
                            }
                            viewElements.textNewMeetingName.setText("");

                            activity.setApplicationState(new Map(activity, ((MapFragment) activity.getFragment()).getMapManager(), viewElements));
                            activity.getApplicationState().init();
                            progressDialog.dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG, t.getMessage());
                    progressDialog.dismiss();
                    Toast.makeText(activity, "Failed", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
