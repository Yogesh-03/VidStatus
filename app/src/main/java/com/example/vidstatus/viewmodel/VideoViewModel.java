package com.example.vidstatus.viewmodel;

import android.util.Log;
import android.widget.VideoView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vidstatus.model.Root;
import com.example.vidstatus.repository.VideoRepository;

public class VideoViewModel extends ViewModel {
    VideoRepository videoRepository;
    public VideoViewModel(VideoRepository videoRepository){
        this.videoRepository = videoRepository;
        getVideos();
    }

    private MutableLiveData<Integer> _responseData = new MutableLiveData<>(0);
    public MutableLiveData<Root> rootMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Integer> responseData = _responseData;

    public LiveData<Integer> getResponseData() {
        return _responseData;
    }

    private  void getVideos(){
        try {
            videoRepository.getVideos(new VideoRepository.ResponseCallback() {
                @Override
                public void responseCallback(int responseCode, Root root) {
                    rootMutableLiveData.postValue(root);
                    _responseData.postValue(responseCode);
                    Log.d("Resp", _responseData.getValue().toString());

                }
            });
        } catch (Exception e){

        }
    }
}
