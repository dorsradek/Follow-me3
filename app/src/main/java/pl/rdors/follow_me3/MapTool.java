package pl.rdors.follow_me3;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.TimeUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rdors on 2016-11-02.
 */

public class MapTool implements OnMapReadyCallback {

    private LatLng mCenterLatLong;
    private GoogleMap googleMap;

    private TestActivity activity;
    private IntentServiceTool intentServiceTool;
    private TextViewTool textViewTool;

    Geocoder geocoder;

    public static String TAG = "MAP LOCATION";

    public MapTool(TestActivity activity, IntentServiceTool intentServiceTool, TextViewTool textViewTool) {
        this.activity = activity;
        this.intentServiceTool = intentServiceTool;
        this.textViewTool = textViewTool;

        geocoder = new Geocoder(activity, Locale.getDefault());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        this.googleMap = googleMap;

        Thread t = new Thread(r);
        t.start();

        this.googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {

//                CameraPosition cameraPosition = googleMap.getCameraPosition();
//
//                Log.d("Camera postion change" + "", cameraPosition + "");
//                mCenterLatLong = cameraPosition.target;
//
//
//                googleMap.clear();
//
//                try {
//
//                    Location mLocation = new Location("");
//                    mLocation.setLatitude(mCenterLatLong.latitude);
//                    mLocation.setLongitude(mCenterLatLong.longitude);
//
//                    intentServiceTool.startIntentService(mLocation);
//                    textViewTool.getLocationMarkerText().setText(asd(mCenterLatLong));
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });

        this.googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                CameraPosition cameraPosition = googleMap.getCameraPosition();

                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;


                googleMap.clear();

                try {

                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    intentServiceTool.startIntentService(mLocation);
                    asd(mCenterLatLong);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + googleMap);

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[] {
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    2);
            return;
        }

        // check if map is created successfully or not
        if (googleMap != null) {
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            mCenterLatLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(mCenterLatLong).zoom(19f).tilt(70).build();

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            asd(mCenterLatLong);
            intentServiceTool.startIntentService(location);


        } else {
            Toast.makeText(activity.getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    LatLng last = new LatLng(0 ,0);
    String address = "";

    Runnable r = new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (atomicBoolean.getAndSet(false)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mCenterLatLong.latitude != last.latitude
                            && mCenterLatLong.longitude != last.longitude) {
                        last = mCenterLatLong;

                        List<Address> addresses;
                        try {
                            addresses = geocoder.getFromLocation(mCenterLatLong.latitude, mCenterLatLong.longitude, 1);
                            address = addresses.get(0).getAddressLine(0);
                            System.out.println("ASdasd" + address);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewTool.getLocationMarkerText().setText(address);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }



            }
        }
    };

    AtomicBoolean atomicBoolean = new AtomicBoolean();

    private void asd(LatLng latLong) {
        atomicBoolean.set(true);
    }


    public GoogleMap getGoogleMap() {
        return googleMap;
    }

}
