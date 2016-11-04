package pl.rdors.follow_me3.view;

import android.view.View;

/**
 * Created by rdors on 2016-11-04.
 */

public class MapMove implements IMapMovable {

    private ViewElementsManager viewElementsManager;

    public MapMove(ViewElementsManager viewElementsManager) {
        this.viewElementsManager = viewElementsManager;
    }

    @Override
    public void animateWhenMapMoveStarted() {
        viewElementsManager.toolbarContainer.animate()
                .translationY(-viewElementsManager.toolbarContainer.getHeight() - 20)
                .alpha(0.0f)
                .setDuration(viewElementsManager.ANIMATION_TIME);
        viewElementsManager.toolbarContainer.setVisibility(View.INVISIBLE);

        viewElementsManager.buttonNewMeeting.animate()
                .translationY(viewElementsManager.buttonNewMeeting.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(viewElementsManager.ANIMATION_TIME);
        viewElementsManager.buttonNewMeeting.setVisibility(View.INVISIBLE);
    }

    @Override
    public void animateWhenMapIdle() {
        viewElementsManager.toolbarContainer.setVisibility(View.VISIBLE);
        viewElementsManager.toolbarContainer.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(viewElementsManager.ANIMATION_TIME);

        viewElementsManager.buttonNewMeeting.setVisibility(View.VISIBLE);
        viewElementsManager.buttonNewMeeting.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(viewElementsManager.ANIMATION_TIME);
    }

}
