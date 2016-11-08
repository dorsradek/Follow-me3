package pl.rdors.follow_me3.rest.service;

import java.util.List;

import okhttp3.ResponseBody;
import pl.rdors.follow_me3.rest.model.Meeting;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MeetingService {

    @GET("/meetings")
    Call<List<Meeting>> findAll();

    @POST("/meetings/create")
    Call<ResponseBody> create(@Body Meeting meeting);

}