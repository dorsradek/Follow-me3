package pl.rdors.follow_me3.rest.service;

import pl.rdors.follow_me3.rest.model.JwtAuthenticationRequest;
import pl.rdors.follow_me3.rest.model.JwtAuthenticationResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthService {

    @POST("/facebook")
    Call<JwtAuthenticationResponse> facebook(@Query("token") String token);

    @POST("/auth")
    Call<JwtAuthenticationResponse> auth(@Body JwtAuthenticationRequest request);

    @GET("/refresh")
    Call<JwtAuthenticationResponse> refresh(@Header("Authorization") String token);


}