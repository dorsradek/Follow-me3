package pl.rdors.follow_me3.state.map;

import android.view.View;

import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.state.IApplicationState;
import pl.rdors.follow_me3.utils.AppUtils;
import pl.rdors.follow_me3.view.ViewElements;

import static pl.rdors.follow_me3.view.ViewElements.ANIMATION_TIME;

/**
 * Created by rdors on 2016-11-06.
 */

public class Map extends MapState implements IApplicationState {

    public Map(TestActivity activity, ViewElements viewElements) {
        super(activity, viewElements);
    }

    @Override
    public void init() {
        viewElements.buttonNewMeeting.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.buttonCheckMark.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.toolbarContainer.setTranslationY(-AppUtils.getHeightPx(activity));
        viewElements.newMeetingContainer.setTranslationY(AppUtils.getHeightPx(activity));

        viewElements.buttonNewMeeting.setVisibility(View.VISIBLE);
        viewElements.buttonNewMeeting.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(ANIMATION_TIME);

        viewElements.newMeetingContainer.animate()
                .translationY(viewElements.newMeetingContainer.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(ANIMATION_TIME);
        viewElements.newMeetingContainer.setVisibility(View.INVISIBLE);
        viewElements.locationMarkerContainer.setVisibility(View.INVISIBLE);

        viewElements.toolbarContainer.setEnabled(true);

        activity.enableFragment(true);
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
