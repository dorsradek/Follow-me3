package pl.rdors.follow_me3.state.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import java.util.List;

import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.UserArrayAdapter;
import pl.rdors.follow_me3.google.MapManager;
import pl.rdors.follow_me3.rest.ServiceGenerator;
import pl.rdors.follow_me3.rest.model.User;
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

    public NewMeeting(TestActivity activity, MapManager mapManager, ViewElements viewElements) {
        super(activity, mapManager, viewElements);
    }

    @Override
    public void init() {

        initUsers();
        //hide
        viewElements.buttonCheckMark.animate()
                .translationY(viewElements.buttonCheckMark.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(ANIMATION_TIME);
        viewElements.buttonCheckMark.setVisibility(View.INVISIBLE);

        //show
        viewElements.newMeetingContainer.setVisibility(View.VISIBLE);
        viewElements.newMeetingContainer.animate()
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

}
