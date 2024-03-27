package com.example.vidstatus;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vidstatus.adapters.VideoAdapter;
import com.example.vidstatus.api.ApiInterface;
import com.example.vidstatus.api.RetrofitInstance;
import com.example.vidstatus.databinding.ActivityMainBinding;
import com.example.vidstatus.model.ExoPlayerItem;
import com.example.vidstatus.model.Msg;
import com.example.vidstatus.model.Root;
import com.example.vidstatus.repository.VideoRepository;
import com.example.vidstatus.viewmodel.VideoViewModel;
import com.example.vidstatus.viewmodel.VideoViewModelFactory;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 recyclerView;
    private VideoAdapter videoAdapter;
    private List<String> videoUrls;

    private ApiInterface apiInterface;
    private VideoViewModel videoViewModel;
    private ArrayList<ExoPlayerItem> exoPlayerItems = new ArrayList<>();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiInterface = RetrofitInstance.getService().create(ApiInterface.class);
        VideoViewModelFactory videoViewModelFactory = new VideoViewModelFactory(new VideoRepository(apiInterface));
        videoViewModel = new ViewModelProvider(this, videoViewModelFactory).get(VideoViewModel.class);

        recyclerView = findViewById(R.id.viewPager);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoUrls = new ArrayList<>();






        videoViewModel.responseData.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 200){
//                    videoUrls.add("http://g-sgp-cdn.snackvideo.in/upic/2021/04/12/14/BMjAyMTA0MTIxNDA5NTZfMTUwMDAwNTA0NzAyNjU4XzE1MDAwMTI2NTU1NjYyM18wXzM=_b_Bacf7595bffecbbabe0e67e8df6df635a.mp4?tag=1-1618597955-s-0-g97idbc6k7-0afdb8a4dc47ea29");
//                    videoUrls.add("http://g-sgp-cdn.snackvideo.in/upic/2021/01/25/01/BMjAyMTAxMjUwMTA5MTJfMTUwMDAwNDkxODc4Nzc2XzE1MDAwMTIxMTAzMDA2NF8yXzM=_b_B38c5fd44d144393b166efe3de97110aa.mp4?tag=1-1618597675-s-0-vybqxtdt8h-2cbe66a2ce539752");

                    videoViewModel.rootMutableLiveData.observe(MainActivity.this, new Observer<Root>() {
                        @Override
                        public void onChanged(Root root) {


                            for(int i =0; i<root.msg.size(); i++){
                                String videoUrl = root.msg.get(i).video;
                                videoUrls.add(videoUrl);
                            }
                            videoAdapter = new VideoAdapter(MainActivity.this, videoUrls, new VideoAdapter.OnVideoPreparedListener() {
                                @Override
                                public void onVideoPrepared(ExoPlayerItem exoPlayerItem) {
                                    exoPlayerItems.add(exoPlayerItem);
                                }
                            });
                            recyclerView.setAdapter(videoAdapter);

                        }
                    });

                }
            }
        });

        recyclerView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                int previousIndex = -1;
                for (int i = 0; i < exoPlayerItems.size(); i++) {
                    if (exoPlayerItems.get(i).exoPlayer.isPlaying()) {
                        previousIndex = i;
                        break;
                    }
                }

                if (previousIndex !=-1){
                    ExoPlayer player = exoPlayerItems.get(previousIndex).exoPlayer;
                    player.pause();
                    player.setPlayWhenReady(false);
                    Log.d("Video", "paused");
                }


                int newIndex = -1;
                for (int i = 0; i < exoPlayerItems.size(); i++) {
                    if (exoPlayerItems.get(i).position == position) {
                        newIndex = i;
                        break;
                    }
                }
                if(newIndex !=-1){
                    ExoPlayer player = exoPlayerItems.get(newIndex).exoPlayer;
                    player.setPlayWhenReady(true);
                    player.play();
                    Log.d("Video", "Started");
                    int lostIndex = newIndex-1;

                }
            }
        });

    }

    public void releasePlayer(ExoPlayer player) {
        if (player != null) {
            player.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        int index = -1;
        for (int i = 0; i < exoPlayerItems.size(); i++) {
            if (exoPlayerItems.get(i).position == binding.viewPager.getCurrentItem()) {
                index = i;
                break;
            }
        }
        if(index!=-1){
            ExoPlayer player = exoPlayerItems.get(index).exoPlayer;
            player.pause();
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected  void onResume() {

        super.onResume();
        int index = -1;
        for (int i = 0; i < exoPlayerItems.size(); i++) {
            if (exoPlayerItems.get(i).position == binding.viewPager.getCurrentItem()) {
                index = i;
                break;
            }
        }

        if(index!=-1){
            ExoPlayer player = exoPlayerItems.get(index).exoPlayer;
            player.pause();
            player.setPlayWhenReady(true);
            player.play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!exoPlayerItems.isEmpty()){
            for (int i =0; i<exoPlayerItems.size(); i++){
                ExoPlayer player = exoPlayerItems.get(i).exoPlayer;
                player.stop();
                player.clearMediaItems();
            }
        }
    }
}