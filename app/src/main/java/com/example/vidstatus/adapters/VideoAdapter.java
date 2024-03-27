package com.example.vidstatus.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vidstatus.R;
import com.example.vidstatus.cache.CacheDataSourceFactory;
import com.example.vidstatus.model.ExoPlayerItem;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheEvictor;
import com.google.android.exoplayer2.upstream.cache.CacheSpan;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    public   Context context;
    public List<String> videoUrls;
    public OnVideoPreparedListener onVideoPreparedListener;
    private ExoPlayer player;
    private static SimpleCache cache;

    public VideoAdapter(Context context, List<String> videoUrls, OnVideoPreparedListener onVideoPreparedListener) {
        this.context = context;
        this.videoUrls = videoUrls;
        this.onVideoPreparedListener = onVideoPreparedListener;
        //this.player = new SimpleExoPlayer.Builder(context).build();
        if (cache == null) {
            cache = createCache(context);
        }
    }

    private static SimpleCache createCache(Context context) {
        File cacheDir = new File(context.getCacheDir(), "media_cache");
        CacheEvictor evictor = new LeastRecentlyUsedCacheEvictor(3 * 1024 * 1024); // Adjust cache size as per your requirement
        return new SimpleCache(cacheDir, evictor);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new MyViewHolder(view, onVideoPreparedListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.initializePlayer(Uri.parse(videoUrls.get(position)), position);
        if (position + 1 < videoUrls.size()) {
            preloadNextVideo(position, player);
        }
    }

    @Override
    public int getItemCount() {
        return videoUrls.size();
    }

    public interface OnVideoPreparedListener{
        public void onVideoPrepared(ExoPlayerItem exoPlayerItem);
    }


    private void preloadNextVideo(int currentPosition, ExoPlayer player) {
        int nextPosition = currentPosition + 1;
        if (nextPosition < videoUrls.size()) {
            Uri nextVideoUri = Uri.parse(videoUrls.get(nextPosition));

            // Create a DataSource factory with cache enabled
            // Create a DataSource factory with cache enabled
            DataSource.Factory dataSourceFactory = new CacheDataSourceFactory(
                    context,
                    cache
//                    new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getString(R.string.app_name))),
//                    CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR
            );

//            DataSource.Factory dataSourceFactory = new CacheDataSourceFactory(// Provide your Cache instance
//                    new DefaultDataSourceFactory(context, Util.getUserAgent(context, context.getString(R.string.app_name))),
//                    CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);

            // Create a MediaSource for the next video
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(nextVideoUri));

            // Prepare the MediaSource asynchronously
            player.prepare(mediaSource);
        }
    }

    private SimpleCache createCache() {
        File cacheDir = new File(context.getCacheDir(), "media_cache");
        CacheEvictor evictor = new CacheEvictor() {
            @Override
            public void onSpanAdded(Cache cache, CacheSpan cacheSpan) {

            }

            @Override
            public void onSpanRemoved(Cache cache, CacheSpan cacheSpan) {

            }

            @Override
            public void onSpanTouched(Cache cache, CacheSpan cacheSpan, CacheSpan cacheSpan1) {

            }

            @Override
            public boolean requiresCacheSpanTouches() {
                return false;
            }

            @Override
            public void onCacheInitialized() {
                // Do nothing
            }

            @Override
            public void onStartFile(Cache cache, String s, long l, long l1) {

            }

        };
        return new SimpleCache(cacheDir, evictor);
    }

    public class  MyViewHolder extends RecyclerView.ViewHolder{



        public PlayerView playerView;
//        private ExoPlayer player;
       public boolean playWhenReady = true; // Default to play when ready
        public OnVideoPreparedListener onVideoPreparedListener;

        public MyViewHolder(@NonNull View itemView, OnVideoPreparedListener onVideoPreparedListener) {
            super(itemView);
            playerView = itemView.findViewById(R.id.player_view);
            this.onVideoPreparedListener = onVideoPreparedListener;
        }



        public void initializePlayer(Uri videoUri, int position) {
//            player = ExoPlayer.newSimpleInstance(context, new DefaultTrackSelector());
             player = new SimpleExoPlayer.Builder(context).build();

//            player.addListener(new Player.Listener() {
//                @Override
//                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                    if(playbackState == Player.STATE_READY){
//                        preloadNextVideo(position, player);
//                    }
//                }
//
//                @Override
//                public void onPlayerError(PlaybackException error) {
//                    Player.Listener.super.onPlayerError(error);
//                    Toast.makeText(context, "Can't play this video", Toast.LENGTH_SHORT).show();
//
//                }
//            });


            playerView.setPlayer(player);

            String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
            ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(
                    new DefaultDataSourceFactory(context, userAgent))
                    .createMediaSource(MediaItem.fromUri(videoUri));



            player.setMediaSource(mediaSource);
            player.prepare(mediaSource);
            if (getAbsoluteAdapterPosition() == 0){
                player.setPlayWhenReady(true);
                player.play();
            }
            onVideoPreparedListener.onVideoPrepared(new ExoPlayerItem(player, getAbsoluteAdapterPosition()));
        }



    }
}
