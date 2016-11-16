package pl.rdors.follow_me3.state.map;

import android.location.Location;
import android.os.Handler;
import android.view.View;

import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.fragment.MapFragment;
import pl.rdors.follow_me3.google.MapManager;
import pl.rdors.follow_me3.intentservice.AddressResultReceiver;
import pl.rdors.follow_me3.intentservice.IntentServiceTool;
import pl.rdors.follow_me3.utils.AppUtils;
import pl.rdors.follow_me3.view.ViewElements;

import static pl.rdors.follow_me3.view.ViewElements.ANIMATION_TIME;

/**
 * Created by rdors on 2016-11-06.
 */

public class MeetingMap extends MapState {

    private IntentServiceTool intentServiceTool;
    private AddressResultReceiver addressResultReceiver;

    public MeetingMap(TestActivity activity, MapManager mapManager, ViewElements viewElements) {
        super(activity, mapManager, viewElements);

        addressResultReceiver = new AddressResultReceiver(new Handler(), viewElements);
        intentServiceTool = new IntentServiceTool(addressResultReceiver, activity);
    }

    @Override
    public void init() {
        viewElements.buttonCheckMark.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.buttonCheckMark.setVisibility(View.INVISIBLE);
        viewElements.containerLocationToolbar.setTranslationY(-AppUtils.getHeightPx(activity));
        viewElements.containerLocationToolbar.setVisibility(View.INVISIBLE);
        viewElements.containerNewMeeting.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.containerNewMeeting.setVisibility(View.INVISIBLE);

        //hide
        viewElements.buttonNewMeeting.animate()
                .translationY(viewElements.buttonNewMeeting.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(ANIMATION_TIME);
        viewElements.buttonNewMeeting.setVisibility(View.INVISIBLE);

        //show
        viewElements.buttonCheckMark.setVisibility(View.VISIBLE);
        viewElements.buttonCheckMark.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        //show
        viewElements.containerLocationToolbar.setVisibility(View.VISIBLE);
        viewElements.containerLocationToolbar.setEnabled(true);
        viewElements.containerLocationToolbar.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        //show
        viewElements.locationMarkerContainer.setTranslationY(0);
        viewElements.locationMarkerContainer.setVisibility(View.VISIBLE);
        viewElements.buttonCheckMark.animate()
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        startIntentServiceOnLocation();
        enable(true);
    }

    @Override
    public void animateWhenMapMoveStarted() {
        viewElements.containerLocationToolbar.animate()
                .translationY(-viewElements.containerLocationToolbar.getHeight() - 20)
                .alpha(0.0f)
                .setDuration(viewElements.ANIMATION_TIME);
        viewElements.containerLocationToolbar.setVisibility(View.INVISIBLE);

        viewElements.buttonCheckMark.animate()
                .translationY(viewElements.buttonCheckMark.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(viewElements.ANIMATION_TIME);
        viewElements.buttonCheckMark.setVisibility(View.INVISIBLE);

        viewElements.textAddress.setText("");
    }

    @Override
    public void animateWhenMapIdle() {
        startIntentServiceOnLocation();

        viewElements.containerLocationToolbar.setVisibility(View.VISIBLE);
        viewElements.containerLocationToolbar.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(viewElements.ANIMATION_TIME);

        viewElements.buttonCheckMark.setVisibility(View.VISIBLE);
        viewElements.buttonCheckMark.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(viewElements.ANIMATION_TIME);
    }

    private void startIntentServiceOnLocation() {
        Location location = new Location("");
        location.setLatitude(mapManager.latLngCenter.latitude);
        location.setLongitude(mapManager.latLngCenter.longitude);
        intentServiceTool.startIntentService(location);
    }

    @Override
    public boolean canBack() {
        return true;
    }

    @Override
    public void back() {
        activity.setApplicationState(new Map(activity, mapManager, viewElements));
        activity.getApplicationState().init();
    }

    @Override
    public void buttonCheckMarkOnClick() {
        if (activity.getFragment() != null
                && activity.getFragment() instanceof MapFragment) {
            activity.setApplicationState(new NewMeeting(activity, ((MapFragment) activity.getFragment()).getMapManager(), viewElements));
            activity.getApplicationState().init();
        }
    }

}
