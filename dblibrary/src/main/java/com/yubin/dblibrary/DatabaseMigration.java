package com.yubin.dblibrary;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * <pre>
 *     @author : dingyubin
 *     e-mail : dingyubin@gmail.com
 *     time    : 2022/02/08
 *     desc    : 版本 执行sql
 *     version : 1.0
 * </pre>
 */
public class DatabaseMigration {
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("delete from conversation");
            database.execSQL("delete from chat_record");
            database.execSQL("ALTER TABLE chat_record ADD COLUMN msg_seq INTEGER NOT NULL DEFAULT -1");
        }
    };
}
