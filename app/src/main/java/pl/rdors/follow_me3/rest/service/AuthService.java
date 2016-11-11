package pl.rdors.follow_me3.rest.service;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {

    @POST("/facebook")
    Call<Object> facebook(@Query("token") String token);

}