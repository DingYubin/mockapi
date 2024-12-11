package com.yubin.kotlindb

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * <pre>
 * @author : dingyubin
 * e-mail : dingyubin@gmail.com
 * time    : 2022/02/08
 * desc    : 版本 执行sql
 * version : 1.0
</pre> *
 */
object DatabaseMigration {
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
//            db.execSQL("delete from conversation")
            db.execSQL("delete from oe_record")
//            db.execSQL("ALTER TABLE oe_record ADD COLUMN msg_seq INTEGER NOT NULL DEFAULT -1")
        }
    }
}