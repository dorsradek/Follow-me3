package pl.rdors.follow_me3.state.map;

import android.location.Location;
import android.os.Handler;
import android.view.View;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import pl.rdors.follow_me3.TestActivity;
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
        viewElements.toolbarContainer.setTranslationY(-AppUtils.getHeightPx(activity));
        viewElements.toolbarContainer.setVisibility(View.INVISIBLE);
        viewElements.newMeetingContainer.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.newMeetingContainer.setVisibility(View.INVISIBLE);

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
        viewElements.toolbarContainer.setVisibility(View.VISIBLE);
        viewElements.toolbarContainer.setEnabled(true);
        viewElements.toolbarContainer.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        //show
        viewElements.locationMarkerContainer.setTranslationY(0);
        viewElements.locationMarkerContainer.setVisibility(View.VISIBLE);
        viewElements.buttonCheckMark.animate()
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        enable(true);
    }

    @Override
    public void animateWhenMapMoveStarted() {
        viewElements.toolbarContainer.animate()
                .translationY(-viewElements.toolbarContainer.getHeight() - 20)
                .alpha(0.0f)
                .setDuration(viewElements.ANIMATION_TIME);
        viewElements.toolbarContainer.setVisibility(View.INVISIBLE);

        viewElements.buttonCheckMark.animate()
                .translationY(viewElements.buttonCheckMark.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(viewElements.ANIMATION_TIME);
        viewElements.buttonCheckMark.setVisibility(View.INVISIBLE);

        viewElements.locationAddress.setText("");
    }

    @Override
    public void animateWhenMapIdle() {
        Location location = createLocation(mapManager.getGoogleMap().getCameraPosition());
        mapManager.latLngCenter = new LatLng(location.getLatitude(), location.getLongitude());
        intentServiceTool.startIntentService(location);

        viewElements.toolbarContainer.setVisibility(View.VISIBLE);
        viewElements.toolbarContainer.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(viewElements.ANIMATION_TIME);

        viewElements.buttonCheckMark.setVisibility(View.VISIBLE);
        viewElements.buttonCheckMark.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(viewElements.ANIMATION_TIME);
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

    private Location createLocation(CameraPosition cameraPosition) {
        LatLng latLng = cameraPosition.target;
        Location location = new Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

}
