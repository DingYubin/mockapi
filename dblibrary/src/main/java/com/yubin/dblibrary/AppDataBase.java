package com.yubin.dblibrary;

import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.yubin.baselibrary.global.AppGlobals;
import com.yubin.database.safe.SafeHelperFactory;
import com.yubin.dblibrary.convert.MessageConvert;
import com.yubin.dblibrary.dao.ChatRecordDao;
import com.yubin.dblibrary.dao.ConversationDao;
import com.yubin.dblibrary.entity.ChatRecord;
import com.yubin.dblibrary.entity.Conversation;

/**
 * <pre>
 *     author  : dingyubin
 *     e-mail  : dingyubin@gmail.com
 *     time    : 2022/01/24
 *     desc    : 数据库
 *     version : 1.0
 *     migration: 1->2 消息表增加消息seq
 * </pre>
 */
@Database(entities = {
        Conversation.class, ChatRecord.class
}, version = 1)

@TypeConverters(MessageConvert.class)
public abstract class AppDataBase extends RoomDatabase {

    public abstract ConversationDao conversationDao();

    public abstract ChatRecordDao chatRecordDao();

    private static AppDataBase appDataBase;

    public synchronized static AppDataBase getInstance() {
        if (appDataBase == null || !appDataBase.isOpen()) {
            open();
        }
        return appDataBase;
    }

    public synchronized static void open() {
        release();
        //从缓存里取
        String uid = "dyb001";
        //这里做加密处理
        SafeHelperFactory factory = new SafeHelperFactory(uid.toCharArray());
        appDataBase = Room.databaseBuilder(AppGlobals.getApplication(), AppDataBase.class, uid)
//                .openHelperFactory(factory)
                .addMigrations(DatabaseMigration.MIGRATION_1_2)
                .allowMainThreadQueries()
                .build();
    }

    public static void release() {
        Log.d("AppDataBase"," release " + (null == appDataBase) + (null == appDataBase ? ""
                : " isopen : " + appDataBase.isOpen()));
        if (null != appDataBase && appDataBase.isOpen()) {
            appDataBase.close();
            appDataBase = null;
            Log.d("AppDataBase","==========   release   ===========");
        }
    }

}
