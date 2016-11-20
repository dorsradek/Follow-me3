package pl.rdors.follow_me3;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Calendar;
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

    private String username;
    private String token;
    private AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                prefs = TestActivity.this.getSharedPreferences("follow-me", Context.MODE_PRIVATE);

                username = prefs.getString("username", "");
                token = prefs.getString("token", "");
                accessToken = AccessToken.getCurrentAccessToken();

                if (accessToken != null
                        && accessToken.getToken() != null
                        && !accessToken.getToken().isEmpty()) {
                    AccessToken.refreshCurrentAccessTokenAsync();
                }

                if (username == null || username.isEmpty()) {
                    goToLogin();
                } else {
                    handleExistenceOfUsername();
                }
                return null;
            }
        };
        asyncTask.execute();

    }

    private void handleExistenceOfUsername() {
        if (token == null || token.isEmpty()) {
            handleNoExistenceOfTokenWS();
        } else {
            handleExistenceOfTokenWS();
        }
    }

    private void handleExistenceOfTokenWS() {
        //pobierz token WS na podstawie tokena WS
        AuthService authService = ServiceGenerator.createService(AuthService.class);
        authService.refresh(token).enqueue(new Callback<JwtAuthenticationResponse>() {
            @Override
            public void onResponse(Call<JwtAuthenticationResponse> call, Response<JwtAuthenticationResponse> response) {
                handleAuthenticationData(response);
                if (token == null || token.isEmpty()) {
                    handleNoExistenceOfTokenWS();
                } else {
                    goToApplication();
                }
            }

            @Override
            public void onFailure(Call<JwtAuthenticationResponse> call, Throwable t) {
                handleNoExistenceOfTokenWS();
            }
        });
    }

    private void handleAuthenticationData(Response<JwtAuthenticationResponse> response) {
        JwtAuthenticationResponse authenticationResponse = response.body();
        if (authenticationResponse != null) {
            token = authenticationResponse.getToken();
            username = authenticationResponse.getUser().getUsername();
        }
    }

    private void handleNoExistenceOfTokenWS() {
        if (accessToken == null
                || accessToken.getToken() == null
                || accessToken.getToken().isEmpty()
                || accessToken.isExpired()) {
            goToLogin();
        } else {
            handleExistenceOfTokenFB();
        }
    }

    private void handleExistenceOfTokenFB() {
        //pobierz toke WS na podstawie tokena FB
        AuthService authService = ServiceGenerator.createService(AuthService.class);
        authService.facebook(accessToken.getToken()).enqueue(new Callback<JwtAuthenticationResponse>() {
            @Override
            public void onResponse(Call<JwtAuthenticationResponse> call, Response<JwtAuthenticationResponse> response) {
                handleAuthenticationData(response);
                if (token == null || token.isEmpty()) {
                    goToLogin();
                } else {
                    goToApplication();
                }
            }

            @Override
            public void onFailure(Call<JwtAuthenticationResponse> call, Throwable t) {
                goToLogin();
            }
        });
    }

    private void logout() {
        clearMapData();

        alarmManager.cancel(pendingIntent);

        prefs.edit().remove("token").apply();
        prefs.edit().remove("username").apply();
        prefs.edit().commit();

        goToLogin();
    }

    private void clearMapData() {
        if (fragment != null &&
                fragment instanceof MapFragment &&
                ((MapFragment) fragment).getMapManager() != null &&
                ((MapFragment) fragment).getMapManager().getGoogleMap() != null) {
            ((MapFragment) fragment).getMapManager().getGoogleMap().clear();
        }

        MeetingManager.getMeetingUserPolylines().clear();
        UserManager.getUsers().clear();
        MeetingManager.getMeetings().clear();
    }

    private void goToLogin() {
        Intent intent = new Intent(TestActivity.this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_RESULT);
    }

    private void goToApplication() {
        prefs.edit().putString("token", token).apply();
        prefs.edit().putString("username", username).apply();
        prefs.edit().commit();

        startLocationTracker();
        LocationProvider locationProvider = LocationProvider.getInstance();
        locationProvider.configureIfNeeded(this);

        loadMeetings();

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

    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Intent intent;

    private void startLocationTracker() {
        // Configure the LocationTracker's broadcast receiver to run every 5 minutes.
        intent = new Intent(this, LocationTracker.class);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(),
                LocationProvider.asd_MINUTES, pendingIntent);
    }

    public ProgressDialog progressDialog;

    private void loadMeetings() {
        progressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        MeetingService meetingService = ServiceGenerator.createService(MeetingService.class);
        meetingService.findAll(token).enqueue(new Callback<List<Meeting>>() {
            @Override
            public void onResponse(Call<List<Meeting>> call, Response<List<Meeting>> response) {
                for (Meeting meeting : response.body()) {
                    MeetingManager.addMeeting(meeting);
                }
                loadMapFragment();
            }

            @Override
            public void onFailure(Call<List<Meeting>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                loadMapFragment();
            }
        });
    }

    private void loadMapFragment() {
        new Handler().post(new Runnable() {
            public void run() {
                fragment = MapFragment.newInstance();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, fragment);
                ft.commitAllowingStateLoss();
            }
        });
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
        switch (requestCode) {
            case (LOGIN_RESULT): {
                if (resultCode == Activity.RESULT_OK) {
                    username = prefs.getString("username", "");
                    token = prefs.getString("token", "");
                    goToApplication();
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
                clearMapData();
                loadMeetings();
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
            case R.id.action_logout:
                logout();
                fragment = null;
                break;
        }

        if (fragment != null && !(fragment instanceof MapFragment)) {
            fragment = MapFragment.newInstance();
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
//        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
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
