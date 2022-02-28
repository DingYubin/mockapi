package com.yubin.medialibrary.album.cache;

import android.content.Context;

import java.io.File;

import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.io.FileSystem;


/**
 * description
 *
 * @author laiwei
 * @date create at 4/24/21 11:24 AM
 */
public class CacheManager {
    private static DiskLruCache instance;

    private CacheManager() {

    }

    public static synchronized DiskLruCache getDiskLruCache(Context context) {
        if (instance == null) {
            File file = context.getExternalFilesDir("Thumbnail");
            instance = new DiskLruCache(FileSystem.SYSTEM, file, 1, 1, (64 * 1024 * 1024), TaskRunner.INSTANCE);
        }
        return instance;
    }
}
