package pl.rdors.follow_me3.state.map;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import java.util.List;

import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.google.MapManager;
import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.utils.AppUtils;
import pl.rdors.follow_me3.view.ViewElements;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pl.rdors.follow_me3.view.ViewElements.ANIMATION_TIME;

/**
 * Created by rdors on 2016-11-06.
 */

public class Map extends MapState {

    public Map(TestActivity activity, MapManager mapManager, ViewElements viewElements) {
        super(activity, mapManager, viewElements);
    }

    @Override
    public void init() {
        SharedPreferences prefs = activity.getSharedPreferences("follow-me", Context.MODE_PRIVATE);
        String token = prefs.getString("token", "");
        Call<List<Meeting>> call = activity.getMeetingService().findAll(token);
        call.enqueue(new Callback<List<Meeting>>() {
            @Override
            public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
                if (response.isSuccessful()) {
                    System.out.println(response.body());
                    mapManager.focusOnMeetings(response.body());
                } else {
                    // error response, no access to resource?
                }
            }

            @Override
            public void onFailure(Call<List<Meeting>> call, Throwable t) {
                // something went completely south (like no internet connection)
                //Log.d("Error", t.getMessage());
            }
        });

        viewElements.buttonNewMeeting.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.buttonNewMeeting.setVisibility(View.INVISIBLE);
        viewElements.buttonCheckMark.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.buttonCheckMark.setVisibility(View.INVISIBLE);
        viewElements.toolbarContainer.setTranslationY(-AppUtils.getHeightPx(activity));
        viewElements.toolbarContainer.setVisibility(View.INVISIBLE);
        viewElements.newMeetingContainer.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.newMeetingContainer.setVisibility(View.INVISIBLE);
        viewElements.locationMarkerContainer.setVisibility(View.INVISIBLE);

        viewElements.buttonNewMeeting.setVisibility(View.VISIBLE);
        viewElements.buttonNewMeeting.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        viewElements.toolbarContainer.setVisibility(View.VISIBLE);
        viewElements.toolbarContainer.setEnabled(true);
        viewElements.toolbarContainer.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        enable(true);
    }

    @Override
    public void animateWhenMapMoveStarted() {
        viewElements.toolbarContainer.animate()
                .translationY(-viewElements.toolbarContainer.getHeight() - 20)
                .alpha(0.0f)
                .setDuration(ANIMATION_TIME);
        viewElements.toolbarContainer.setVisibility(View.INVISIBLE);

        viewElements.buttonNewMeeting.animate()
                .translationY(viewElements.buttonNewMeeting.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(ANIMATION_TIME);
        viewElements.buttonNewMeeting.setVisibility(View.INVISIBLE);
    }

    @Override
    public void animateWhenMapIdle() {
        viewElements.toolbarContainer.setVisibility(View.VISIBLE);
        viewElements.toolbarContainer.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        viewElements.buttonNewMeeting.setVisibility(View.VISIBLE);
        viewElements.buttonNewMeeting.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);
    }

    @Override
    public boolean canBack() {
        return false;
    }

    @Override
    public void back() {

    }

}
