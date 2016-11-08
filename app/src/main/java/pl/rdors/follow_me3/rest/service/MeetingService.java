package pl.rdors.follow_me3.rest.service;

import java.util.List;

import pl.rdors.follow_me3.rest.model.Meeting;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MeetingService {

    @GET("meetings")
    Call<List<Meeting>> findAll();

}