package pl.rdors.follow_me3.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import pl.rdors.follow_me3.MeetingManager;
import pl.rdors.follow_me3.R;
import pl.rdors.follow_me3.TestActivity;
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

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        callAsynchronousTask();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
        }
        Log.d(TAG, "onStop");
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
                                List<User> users = response.body();
                                Map<User, Marker> friends = MeetingManager.getFriends();
                                for (User user : users) {
                                    if (friends.containsKey(user)) {
                                        for (Map.Entry<User, Marker> entry : friends.entrySet()) {
                                            User friend = entry.getKey();
                                            if (friend.equals(user)) {
                                                Marker marker = friends.get(user);
                                                friend.setLastUpdate(user.getLastUpdate());
                                                if (friend.getX() != user.getX() || friend.getY() != user.getY()) {
                                                    friend.setX(user.getX());
                                                    friend.setY(user.getY());
                                                    animateMarker(marker, new LatLng(friend.getX(), friend.getY()), false);
                                                }
                                                break;
                                            }
                                        }
                                    } else {
                                        MarkerOptions markerOptions = new MarkerOptions()
                                                .position(new LatLng(user.getX(), user.getY()))
                                                .title(user.getUsername());
                                        Marker marker = mapManager.getGoogleMap().addMarker(markerOptions);
                                        friends.put(user, marker);
                                    }
                                }
                                Log.d(TAG, "Friends: " + friends);
                            }

                            @Override
                            public void onFailure(Call<List<User>> call, Throwable t) {
                                Log.d(TAG, t.getMessage());
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

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mapManager.getGoogleMap().getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
}