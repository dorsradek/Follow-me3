package pl.rdors.follow_me3.state.map;

import android.view.View;

import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.google.MapManager;
import pl.rdors.follow_me3.state.IApplicationState;
import pl.rdors.follow_me3.utils.AppUtils;
import pl.rdors.follow_me3.view.ViewElements;

/**
 * Created by rdors on 2016-11-06.
 */

public class LaunchingMap extends MapState implements IApplicationState {

    public LaunchingMap(TestActivity activity, MapManager mapManager, ViewElements viewElements) {
        super(activity, mapManager, viewElements);
    }

    @Override
    public void init() {
        viewElements.buttonNewMeeting.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.buttonNewMeeting.setVisibility(View.INVISIBLE);
        viewElements.buttonCheckMark.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.buttonCheckMark.setVisibility(View.INVISIBLE);
        viewElements.containerLocationToolbar.setTranslationY(-AppUtils.getHeightPx(activity));
        viewElements.containerLocationToolbar.setVisibility(View.INVISIBLE);
        viewElements.containerNewMeeting.setTranslationY(AppUtils.getHeightPx(activity));
        viewElements.containerNewMeeting.setVisibility(View.INVISIBLE);
        viewElements.locationMarkerContainer.setTranslationY(AppUtils.getHeightPx(activity));
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
        return false;
    }

    @Override
    public void back() {

    }

    @Override
    public void buttonCheckMarkOnClick() {

    }
}
