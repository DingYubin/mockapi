/*
 * Copyright (C) 2016 The Android Open Source Project
 * Modifications Copyright (c) 2017 CommonsWare, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yubin.database.safe;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * SupportSQLiteOpenHelper implementation that works with SQLCipher for Android
 */
class Helper implements SupportSQLiteOpenHelper {
    private final OpenHelper delegate;
    private final char[] passphrase;
    private final String name;

    Helper(Context context, String name, int version,
           SupportSQLiteOpenHelper.Callback callback, char[] passphrase) {
        SQLiteDatabase.loadLibs(context);
        delegate = createDelegate(context, name, version, callback);
        this.passphrase = passphrase;
        this.name = name;
    }

    private OpenHelper createDelegate(Context context, String name,
                                      int version, final Callback callback) {
        return (new OpenHelper(context, name, version) {
            /**
             * {@inheritDoc}
             */
            @Override
            public void onCreate(SQLiteDatabase db) {
                callback.onCreate(getWrappedDb(db));
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                if (!db.isOpen()) {
                    return;
                }
                callback.onUpgrade(getWrappedDb(db), oldVersion, newVersion);
            }

/* MLM -- these methods do not exist in SQLCipher for Android
      @Override
      public void onConfigure(SQLiteDatabase db) {
        callback.onConfigure(getWrappedDb(db));
      }
      @Override
      public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        callback.onDowngrade(getWrappedDb(db), oldVersion, newVersion);
      }
*/

            /**
             * {@inheritDoc}
             */
            @Override
            public void onOpen(SQLiteDatabase db) {
                callback.onOpen(getWrappedDb(db));
            }
        });
    }

    /**
     * {@inheritDoc}
     * <p>
     * NOTE: Not presently supported, will throw an UnsupportedOperationException
     */
    @Override
    public String getDatabaseName() {
        return name;
        // TODO not supported in SQLCipher for Android
//    throw new UnsupportedOperationException("I kinna do it, cap'n!");
//    return delegate.getDatabaseName();
    }

    /**
     * {@inheritDoc}
     * <p>
     * NOTE: Not presently supported, will throw an UnsupportedOperationException
     */
    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setWriteAheadLoggingEnabled(boolean enabled) {
        // TODO not supported in SQLCipher for Android
//        throw new UnsupportedOperationException("I kinna do it, cap'n!");
//        delegate.setWriteAheadLoggingEnabled(enabled);
        Cursor cursor = getWritableDatabase().query("PRAGMA journal_mode = WAL;");
        if (cursor != null && !cursor.isClosed()) {
            try {
                cursor.close();
            } catch (Throwable e) {
            }
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * NOTE: this implementation zeros out the passphrase after opening the
     * database
     */
    @Override
    public SupportSQLiteDatabase getWritableDatabase() {
        SupportSQLiteDatabase result =
                delegate.getWritableSupportDatabase(passphrase);

        for (int i = 0; i < passphrase.length; i++) {
            passphrase[i] = (char) 0;
        }

        return (result);
    }

    /**
     * {@inheritDoc}
     * <p>
     * NOTE: this implementation delegates to getWritableDatabase(), to ensure
     * that we only need the passphrase once
     */
    @Override
    public SupportSQLiteDatabase getReadableDatabase() {
        //return delegate.getReadableSupportDatabase();
        return (getWritableDatabase());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        delegate.close();
    }

    abstract static class OpenHelper extends SQLiteOpenHelper {
        private Database wrappedDb;

        OpenHelper(Context context, String name, int version) {
            super(context, name, null, version, null);
        }

        SupportSQLiteDatabase getWritableSupportDatabase(char[] passphrase) {
            SQLiteDatabase db = super.getWritableDatabase(passphrase);

            return (getWrappedDb(db));
        }

        Database getWrappedDb(SQLiteDatabase db) {
            if (wrappedDb == null) {
                wrappedDb = new Database(db);
            }

            return (wrappedDb);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public synchronized void close() {
            super.close();
            wrappedDb.close();
            wrappedDb = null;
        }

        public void setWriteAheadLoggingEnabled(boolean wal) {
            if (wal) {
                wrappedDb.execSQL("PRAGMA journal_mode = WAL;");
            }
        }
    }
}
