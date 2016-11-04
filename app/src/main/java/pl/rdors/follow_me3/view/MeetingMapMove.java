package pl.rdors.follow_me3.view;

import android.view.View;

/**
 * Created by rdors on 2016-11-04.
 */

public class MeetingMapMove implements IMapMovable {

    private ViewElementsManager viewElementsManager;

    public MeetingMapMove(ViewElementsManager viewElementsManager) {
        this.viewElementsManager = viewElementsManager;
    }

    @Override
    public void animateWhenMapMoveStarted() {
        viewElementsManager.toolbarContainer.animate()
                .translationY(-viewElementsManager.toolbarContainer.getHeight() - 20)
                .alpha(0.0f)
                .setDuration(viewElementsManager.ANIMATION_TIME);
        viewElementsManager.toolbarContainer.setVisibility(View.INVISIBLE);

        viewElementsManager.buttonCheckMark.animate()
                .translationY(viewElementsManager.buttonCheckMark.getHeight() + 20)
                .alpha(0.0f)
                .setDuration(viewElementsManager.ANIMATION_TIME);
        viewElementsManager.buttonCheckMark.setVisibility(View.INVISIBLE);
    }

    @Override
    public void animateWhenMapIdle() {
        viewElementsManager.toolbarContainer.setVisibility(View.VISIBLE);
        viewElementsManager.toolbarContainer.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(viewElementsManager.ANIMATION_TIME);

        viewElementsManager.buttonCheckMark.setVisibility(View.VISIBLE);
        viewElementsManager.buttonCheckMark.animate()
                .translationY(0)
                .alpha(1.0f)
                .setDuration(viewElementsManager.ANIMATION_TIME);
    }

}
