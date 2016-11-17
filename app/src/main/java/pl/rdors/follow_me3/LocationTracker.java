package pl.rdors.follow_me3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.PowerManager;
import android.util.Log;

import okhttp3.ResponseBody;
import pl.rdors.follow_me3.rest.ServiceGenerator;
import pl.rdors.follow_me3.rest.service.LocationService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by rdors on 2016-11-16.
 */
public class LocationTracker extends BroadcastReceiver {

    private static final String TAG = "LocationTracker";

    private PowerManager.WakeLock wakeLock;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pow = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pow.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wakeLock.acquire();

        Location currentLocation = LocationProvider.getInstance().getCurrentLocation();
        SharedPreferences prefs = context.getSharedPreferences("follow-me", Context.MODE_PRIVATE);
        final String token = prefs.getString("token", "");
        if (currentLocation != null) {
            LocationService locationService = ServiceGenerator.createService(LocationService.class);
            Call<ResponseBody> call = locationService.updateLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), token);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    wakeLock.release();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG, t.getMessage());
                    wakeLock.release();
                }
            });
        }


    }
}