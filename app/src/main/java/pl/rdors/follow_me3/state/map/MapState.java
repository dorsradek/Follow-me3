package pl.rdors.follow_me3.state.map;

import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.google.MapManager;
import pl.rdors.follow_me3.state.IApplicationState;
import pl.rdors.follow_me3.view.ViewElements;

/**
 * Created by rdors on 2016-11-07.
 */
public abstract class MapState implements IApplicationState {

    protected TestActivity activity;
    protected MapManager mapManager;
    protected ViewElements viewElements;

    public MapState(TestActivity activity, MapManager mapManager, ViewElements viewElements) {
        this.activity = activity;
        this.mapManager = mapManager;
        this.viewElements = viewElements;
    }

    @Override
    public void enable(boolean enable) {
        mapManager.getGoogleMap().getUiSettings().setScrollGesturesEnabled(enable);
        mapManager.getGoogleMap().getUiSettings().setIndoorLevelPickerEnabled(enable);
        mapManager.getGoogleMap().getUiSettings().setZoomGesturesEnabled(enable);
    }

}
