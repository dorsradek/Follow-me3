package pl.rdors.follow_me3.state.map;

import android.view.View;

import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.state.IApplicationState;
import pl.rdors.follow_me3.view.ViewElements;

import static pl.rdors.follow_me3.view.ViewElements.ANIMATION_TIME;

/**
 * Created by rdors on 2016-11-06.
 */

public class NewMeeting extends MapState implements IApplicationState {

    public NewMeeting(TestActivity activity, ViewElements viewElements) {
        super(activity, viewElements);
    }

    @Override
    public void init() {
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

        viewElements.toolbarContainer.setEnabled(false);
        activity.enableFragment(false);
        viewElements.locationMarkerContainer.setVisibility(View.INVISIBLE);
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
        activity.setApplicationState(new MeetingMap(activity, viewElements));
        activity.getApplicationState().init();
    }

}
