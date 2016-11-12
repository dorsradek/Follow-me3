package pl.rdors.follow_me3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;

import pl.rdors.follow_me3.fragment.EventsFragment;
import pl.rdors.follow_me3.fragment.IOnActivityResult;
import pl.rdors.follow_me3.fragment.MapFragment;
import pl.rdors.follow_me3.fragment.NewsFragment;
import pl.rdors.follow_me3.rest.ServiceGenerator;
import pl.rdors.follow_me3.rest.model.JwtAuthenticationResponse;
import pl.rdors.follow_me3.rest.model.Meeting;
import pl.rdors.follow_me3.rest.service.AuthService;
import pl.rdors.follow_me3.rest.service.MeetingService;
import pl.rdors.follow_me3.state.IApplicationState;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";

    private static final int LOGIN_RESULT = 11;

    private Drawer result = null;
    private Fragment fragment;
    private IApplicationState applicationState;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        prefs = this.getSharedPreferences("follow-me", Context.MODE_PRIVATE);

        prefs.edit().putString("username", "").apply();

        final String token = prefs.getString("token", "");

        AuthService authService = ServiceGenerator.createService(AuthService.class);
        authService.refresh(token).enqueue(new Callback<JwtAuthenticationResponse>() {
            @Override
            public void onResponse(Call<JwtAuthenticationResponse> call, Response<JwtAuthenticationResponse> response) {
                handleAuthenticationResponse(response);
            }

            @Override
            public void onFailure(Call<JwtAuthenticationResponse> call, Throwable t) {
                String username = prefs.getString("username", "");
                if (username == null || username.isEmpty()) {
                    onLoginFailed();
                } else {
                    onLoginSuccess();
                }
            }
        });

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

    private void handleAuthenticationResponse(Response<JwtAuthenticationResponse> response) {
        JwtAuthenticationResponse jwtAuthenticationResponse = response.body();
        if (jwtAuthenticationResponse != null) {
            String token = jwtAuthenticationResponse.getToken();
            prefs.edit().putString("token", token).apply();
            prefs.edit().putString("username", jwtAuthenticationResponse.getUser().getUsername()).apply();
            loadMeetings(token);
        } else {
            onLoginFailed();
        }
    }

    private void loadMeetings(String token) {
        MeetingService meetingService = ServiceGenerator.createService(MeetingService.class);
        meetingService.findAll(token).enqueue(new Callback<List<Meeting>>() {
            @Override
            public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
                MeetingManager.getMeetings().addAll(response.body());
                onLoginSuccess();
            }

            @Override
            public void onFailure(Call<List<Meeting>> call, Throwable t) {
                onLoginSuccess();
            }
        });
    }

    private void onLoginSuccess() {
        fragment = MapFragment.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    public void onLoginFailed() {
        Intent intent = new Intent(TestActivity.this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_RESULT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
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
        } else if (applicationState != null && applicationState.canBack()) {
            applicationState.back();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (LOGIN_RESULT) : {
                if (resultCode == Activity.RESULT_OK) {

                    fragment = MapFragment.newInstance();
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.fragment_container, fragment);
                    ft.commit();
                }
                break;
            }
        }
        if (fragment instanceof IOnActivityResult) {
            ((IOnActivityResult) fragment).apply(requestCode, resultCode, data);
        }
    }

    public void displayView(IDrawerItem drawerItem) {
        String title = getString(R.string.app_name);

        switch ((int) drawerItem.getIdentifier()) {
            case R.id.meetings:
                fragment = MapFragment.newInstance();
                title = "Meetings";
                break;
            case R.id.menu_3:
                fragment = new NewsFragment();
                title = "News";
                break;
            case R.id.menu_2:
                fragment = new EventsFragment();
                title = "Events";
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState");
    }

    public IApplicationState getApplicationState() {
        return applicationState;
    }

    public void setApplicationState(IApplicationState applicationState) {
        this.applicationState = applicationState;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
