package pl.rdors.follow_me3.rest.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface LocationService {

    @PUT("/location")
    Call<ResponseBody> updateLocation(@Query("x") double x, @Query("y") double y, @Header("Authorization") String token);

}