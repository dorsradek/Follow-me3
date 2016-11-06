package pl.rdors.follow_me3.state.map;

import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.view.ViewElements;

/**
 * Created by rdors on 2016-11-07.
 */
public class MapState {

    protected TestActivity activity;
    protected ViewElements viewElements;

    public MapState(TestActivity activity, ViewElements viewElements) {
        this.activity = activity;
        this.viewElements = viewElements;
    }
}
