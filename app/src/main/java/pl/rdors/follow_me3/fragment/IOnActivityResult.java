package pl.rdors.follow_me3.fragment;

import android.content.Intent;

/**
 * Created by rdors on 2016-11-02.
 */

public interface IOnActivityResult {

    void apply(int requestCode, int resultCode, Intent data);

}
