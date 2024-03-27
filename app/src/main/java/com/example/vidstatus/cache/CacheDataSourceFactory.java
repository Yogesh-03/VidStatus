package com.example.vidstatus.cache;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSink;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

public class CacheDataSourceFactory implements com.google.android.exoplayer2.upstream.DataSource.Factory {
    private final Context context;
    private final com.google.android.exoplayer2.upstream.DataSource.Factory upstreamFactory;
    private final Cache cache;
    private final long maxFileSize = 3*1024*1024;

    public CacheDataSourceFactory(Context context, Cache cache) {
        this.context = context;
        this.cache = cache;
        this.upstreamFactory = new DefaultDataSourceFactory(context);

    }

    @Override
    public DataSource createDataSource() {
        return new CacheDataSource(cache, upstreamFactory.createDataSource(),
                new FileDataSource(), new CacheDataSink(cache, maxFileSize),
                CacheDataSource.FLAG_BLOCK_ON_CACHE | CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
    }
}
