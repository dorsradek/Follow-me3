package pl.rdors.follow_me3;

import android.view.View;

import pl.rdors.follow_me3.view.IMapMovable;
import pl.rdors.follow_me3.view.ViewElements;

import static pl.rdors.follow_me3.view.ViewElements.ANIMATION_TIME;

/**
 * Created by rdors on 2016-11-06.
 */

public class NewMeeting implements ApplicationState {

    private TestActivity activity;
    private ViewElements viewElements;

    public NewMeeting(TestActivity activity, ViewElements viewElements) {
        this.activity = activity;
        this.viewElements = viewElements;
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
//        activity.enableFragment(false);
//        state = ViewElementsManager.State.NEW_MEETING_CONTAINER;
//        mapMovable = new MeetingContainerMapMove(this);
        viewElements.locationMarkerContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void animateWhenMapMoveStarted() {

    }

    @Override
    public void animateWhenMapIdle() {

    }

}
