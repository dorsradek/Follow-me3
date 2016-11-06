package pl.rdors.follow_me3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import pl.rdors.follow_me3.fragment.AbleToEnable;
import pl.rdors.follow_me3.fragment.BackPressable;
import pl.rdors.follow_me3.fragment.EventsFragment;
import pl.rdors.follow_me3.fragment.IOnActivityResult;
import pl.rdors.follow_me3.fragment.MapFragment;
import pl.rdors.follow_me3.fragment.NewsFragment;

public class TestActivity extends AppCompatActivity {

    private Drawer result = null;
    private Fragment fragment;
    private ApplicationState applicationState;

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public void setApplicationState(ApplicationState applicationState) {
        this.applicationState = applicationState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sample_dark_toolbar);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.drawer_item_menu_drawer);

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .inflateMenu(R.menu.example_menu)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        displayView(drawerItem);
                        return false;
                    }
                }).build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //displayView(item.getItemId());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Seetings");
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else if (fragment instanceof BackPressable) {
            if (((BackPressable) fragment).allowBackPress()) {
                super.onBackPressed();
            } else {
                ((BackPressable) fragment).backPress();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment instanceof IOnActivityResult) {
            ((IOnActivityResult) fragment).apply(requestCode, resultCode, data);
        }
    }

    public void enableFragment(boolean enable) {
        if (fragment instanceof AbleToEnable) {
            ((AbleToEnable) fragment).enable(enable);
        }
    }

    public void displayView(IDrawerItem drawerItem) {
        String title = getString(R.string.app_name);

        switch ((int) drawerItem.getIdentifier()) {
            case R.id.menu_1:
                fragment = new NewsFragment();
                title = "News";
                break;
            case R.id.menu_2:
                fragment = new EventsFragment();
                title = "Events";
                break;
            case R.id.menu_3:
                fragment = MapFragment.newInstance();
                title = "Map";
                break;

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        result.closeDrawer();

    }

}
