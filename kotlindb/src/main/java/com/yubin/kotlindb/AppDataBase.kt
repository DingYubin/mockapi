package com.yubin.kotlindb

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yubin.baselibrary.global.AppGlobals
import com.yubin.kotlindb.convert.Converters
import com.yubin.kotlindb.dao.OERecordDao
import com.yubin.kotlindb.entity.OERecord

@Database(
    entities = [OERecord::class
    ], version = 1
)
@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun oeRecordDao(): OERecordDao

    companion object {
        @Volatile
        private var instance: AppDataBase? = null

        fun getInstance(): AppDataBase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase().also { instance = it }
            }
        }

        private fun buildDatabase(): AppDataBase {
            return Room.databaseBuilder(
                AppGlobals.getApplication(),
                AppDataBase::class.java,
                "my_room"
            ).addMigrations(DatabaseMigration.MIGRATION_1_2).build()
        }
    }

    fun release() {
        instance?.close()
        instance = null
        Log.d("AppDataBase", "==========   release   ===========")
    }
}