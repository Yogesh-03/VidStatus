package com.example.vidstatus.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.vidstatus.repository.VideoRepository;

public class VideoViewModelFactory implements ViewModelProvider.Factory {
    private VideoRepository videoRepository;

    public VideoViewModelFactory(VideoRepository videoRepository){
        this.videoRepository = videoRepository;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(VideoViewModel.class)){
            return (T) new VideoViewModel(videoRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
