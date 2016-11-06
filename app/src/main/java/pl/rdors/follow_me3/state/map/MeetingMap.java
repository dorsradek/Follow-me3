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

public class MeetingMap extends MapState implements IApplicationState {

    public MeetingMap(TestActivity activity, ViewElements viewElements) {
        super(activity, viewElements);
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

        activity.enableFragment(true);
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
    }

    @Override
    public void animateWhenMapIdle() {
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
        activity.setApplicationState(new Map(activity, viewElements));
        activity.getApplicationState().init();
    }

}
