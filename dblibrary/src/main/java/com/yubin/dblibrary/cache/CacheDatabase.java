package com.yubin.dblibrary.cache;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.yubin.baselibrary.global.AppGlobals;

public abstract class CacheDatabase extends RoomDatabase {
    //创建数据库
    static {
        Room.databaseBuilder(AppGlobals.getApplication(), CacheDatabase.class, "yubin_cache")
            .allowMainThreadQueries()
            .build();
    }

}
