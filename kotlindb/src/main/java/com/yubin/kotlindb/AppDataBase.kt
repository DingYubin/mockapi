package com.yubin.kotlindb

import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yubin.baselibrary.global.AppGlobals
import com.yubin.kotlindb.dao.ChatRecordDao
import com.yubin.kotlindb.dao.ConversationDao

abstract class AppDataBase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao?

    abstract fun chatRecordDao(): ChatRecordDao?

    private var appDataBase: AppDataBase? = null

    @Synchronized
    open fun getInstance(): AppDataBase? {
        if (appDataBase == null || !appDataBase!!.isOpen) {
            open()
        }
        return appDataBase
    }

    @Synchronized
    open fun open() {
        release()
        //从缓存里取
        val uid = "dyb001"
        //这里做加密处理
//        val factory = SafeHelperFactory(uid.toCharArray())
        appDataBase = Room.databaseBuilder(
            AppGlobals.getApplication(),
            AppDataBase::class.java, uid
        ) //                .openHelperFactory(factory)
            .addMigrations(DatabaseMigration.MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()
    }

    open fun release() {

        if (appDataBase?.isOpen == true) {
            appDataBase?.close()
            appDataBase = null
            Log.d("AppDataBase", "==========   release   ===========")
        }
    }
}