package pl.rdors.follow_me3.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
import pl.rdors.follow_me3.UserManager;
import pl.rdors.follow_me3.google.MapManager;
import pl.rdors.follow_me3.rest.ServiceGenerator;
import pl.rdors.follow_me3.rest.model.User;
import pl.rdors.follow_me3.rest.service.FriendshipService;
import pl.rdors.follow_me3.state.map.LaunchingMap;
import pl.rdors.follow_me3.utils.AppUtils;
import pl.rdors.follow_me3.view.ViewElements;
import pl.rdors.follow_me3.view.ViewElementsManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment implements IOnActivityResult {

    private static final String TAG = "MapFragment";

    private TestActivity activity;
    private ViewElementsManager viewElementsManager;
    private MapManager mapManager;

    public ProgressDialog progressDialog;

    private static MapFragment fragment;

    Timer timer;

    public static MapFragment newInstance() {
        //if (fragment == null) {
        fragment = new MapFragment();
        //}
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");

        activity = (TestActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        progressDialog = new ProgressDialog(this.getContext(), R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_map, null, false);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);

        ViewElements viewElements = new ViewElements(view);
        viewElementsManager = new ViewElementsManager(activity, viewElements);
        mapManager = new MapManager(activity, viewElements);

        activity.setApplicationState(new LaunchingMap(activity, mapManager, viewElements));
        activity.getApplicationState().init();

        mapFragment.getMapAsync(mapManager);

        callAsynchronousTask();

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void apply(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppUtils.LocationConstants.REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(activity, data);
                LatLng latLong = place.getLatLng();
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLong).zoom(17f).build();

                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    AppUtils.requestLocationPermission(activity);
                    return;
                }
                mapManager.getGoogleMap().setMyLocationEnabled(true);
                mapManager.getGoogleMap().animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        }
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        SharedPreferences prefs = activity.getSharedPreferences("follow-me", Context.MODE_PRIVATE);
                        String token = prefs.getString("token", "");

                        Call<List<User>> call = ServiceGenerator.createService(FriendshipService.class).findAll(token);

                        call.enqueue(new Callback<List<User>>() {
                            @Override
                            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                                final List<User> users = response.body();
                                UserManager.addOrUpdateUsers(users, mapManager);
                                Log.d(TAG, "Friends: " + UserManager.getUsers());
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {
                                Log.d(TAG, t != null ? t.getMessage() : "error");
                            }
                        });
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60 * 1000);
    }

    public MapManager getMapManager() {
        return mapManager;
    }

}