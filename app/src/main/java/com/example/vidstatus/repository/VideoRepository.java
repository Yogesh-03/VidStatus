package com.example.vidstatus.repository;

import android.util.Log;

import com.example.vidstatus.api.ApiInterface;
import com.example.vidstatus.api.RetrofitInstance;
import com.example.vidstatus.model.Root;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideoRepository {

    public interface ResponseCallback {
        void  responseCallback(int responseCode, Root root);
    }

    private ApiInterface apiInterface;

    public VideoRepository(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    public void getVideos(ResponseCallback callback){
        Call<Root> call = apiInterface.getVideos("showAllVideos");

        call.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                Root root = response.body();
                callback.responseCallback(response.code(), root);
                Log.d("RESPONSE", String.valueOf(response.code()));
                Log.d("RESPONSEBODY", String.valueOf(response.body()));
                Log.d("TTTT", String.valueOf(response.toString()));
            }

            @Override
            public void onFailure(Call<Root> call, Throwable throwable) {
                Log.d("MESS", throwable.getMessage().toString());
                callback.responseCallback(0,null);
            }
        });
    }
}
