package pl.rdors.follow_me3.rest.service;

import java.util.List;

import pl.rdors.follow_me3.rest.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface UserService {

    @GET("/users")
    Call<List<User>> findAll(@Header("Authorization") String token);

}