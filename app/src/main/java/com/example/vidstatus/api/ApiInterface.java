package com.example.vidstatus.api;

import com.example.vidstatus.model.Root;



import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("/app_api/index.php")
     Call<Root> getVideos(@Query("p") String s);
}
