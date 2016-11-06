package pl.rdors.follow_me3;

import android.view.View;

import pl.rdors.follow_me3.utils.AppUtils;
import pl.rdors.follow_me3.view.ViewElements;

/**
 * Created by rdors on 2016-11-06.
 */

public class LaunchingMap implements ApplicationState {

    private TestActivity activity;
    private ViewElements viewElements;

    public LaunchingMap(TestActivity activity, ViewElements viewElements) {
        this.activity = activity;
        this.viewElements = viewElements;
    }

    @Override
    public void init() {
        viewElements.buttonNewMeeting.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.buttonNewMeeting.setVisibility(View.INVISIBLE);
        viewElements.buttonCheckMark.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.buttonCheckMark.setVisibility(View.INVISIBLE);
        viewElements.toolbarContainer.setTranslationY(-AppUtils.getHeightPx(activity));
        viewElements.toolbarContainer.setVisibility(View.INVISIBLE);
        viewElements.newMeetingContainer.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.newMeetingContainer.setVisibility(View.INVISIBLE);
        viewElements.locationMarkerContainer.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.locationMarkerContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void animateWhenMapMoveStarted() {
    }

    @Override
    public void animateWhenMapIdle() {
    }
}
