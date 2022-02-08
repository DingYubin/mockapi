/***
 Copyright (c) 2017 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.yubin.database.safe;

import android.content.Context;
import android.text.Editable;

import androidx.sqlite.db.SupportSQLiteOpenHelper;

/**
 * SupportSQLiteOpenHelper.Factory implementation, for use with Room
 * and similar libraries, that supports SQLCipher for Android.
 */
public class SafeHelperFactory implements SupportSQLiteOpenHelper.Factory {
    final private char[] passphrase;

    /**
     * Creates a SafeHelperFactory from an Editable, such as what you get by
     * calling getText() on an EditText.
     * <p>
     * The Editable will be cleared as part of this call.
     *
     * @param editor the user's supplied passphrase
     * @return a SafeHelperFactory
     */
    public static SafeHelperFactory fromUser(Editable editor) {
        char[] passphrase = new char[editor.length()];

        editor.getChars(0, editor.length(), passphrase, 0);

        SafeHelperFactory result = new SafeHelperFactory(passphrase);

        editor.clear();

        return (result);
    }

    /**
     * Standard constructor.
     * <p>
     * Note that the passphrase supplied here will be filled in with zeros after
     * the database is opened. Ideally, you should not create additional copies
     * of this passphrase, particularly as String objects.
     * <p>
     * If you are using an EditText to collect the passphrase from the user,
     * call getText() on the EditText, and pass that Editable to the
     * SafeHelperFactory.fromUser() factory method.
     *
     * @param passphrase user-supplied passphrase to use for the database
     */
    public SafeHelperFactory(char[] passphrase) {
        this.passphrase = passphrase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SupportSQLiteOpenHelper create(
            SupportSQLiteOpenHelper.Configuration configuration) {
        return (create(configuration.context, configuration.name,
                configuration.callback.version, configuration.callback));
    }

    public SupportSQLiteOpenHelper create(Context context, String name, int version,
                                          SupportSQLiteOpenHelper.Callback callback) {
        return (new Helper(context, name, version, callback, passphrase));
    }
}
