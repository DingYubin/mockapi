package com.getech.android.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/02/26
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ContactUtils {

    private ContactUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }


    /**
     * 添加联系人
     *
     * @param name   联系人名称
     * @param phone  固定电话
     * @param mobile 手机号码
     */
    public static void AddContact(String name, String phone, String mobile) {
        try {
            Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            ContentResolver resolver = Utils.getApp().getContentResolver();
            ContentValues values = new ContentValues();
            long contactId = ContentUris.parseId(resolver.insert(uri, values));
            uri = Uri.parse("content://com.android.contacts/data");
            //添加姓名
            values.put("raw_contact_id", contactId);
            values.put(ContactsContract.RawContacts.Data.MIMETYPE, "vnd.android.cursor.item/name");
            values.put("data1", name);
            resolver.insert(uri, values);
            values.clear();
            if (!TextUtils.isEmpty(mobile)) {
                //手机
                values.put("raw_contact_id", contactId);
                values.put(ContactsContract.RawContacts.Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
                values.put("data1", mobile);
                values.put("data2", "2");
                resolver.insert(uri, values);
                values.clear();
            }
            //单位电话
            if (!TextUtils.isEmpty(phone)) {
                values.put("raw_contact_id", contactId);
                values.put(ContactsContract.RawContacts.Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
                values.put("data1", phone);
                values.put("data2", "3");
                resolver.insert(uri, values);
                values.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param name 联系人姓名
     * @return 是否存在与联系人列表中
     * @throws Exception
     */
    public static boolean hasDataByNameAndPhoneNumber(String name, String phoneNumber) {
        boolean hasData = false;
        if (TextUtils.isEmpty(phoneNumber)) {
            return hasData;
        }
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + name);
        ContentResolver resolver = Utils.getApp().getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data.MIMETYPE, ContactsContract.Data.DATA1}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            Log.e(name, "has");
            do {
                String mimeType = cursor.getString(0);
                String data = cursor.getString(1);
                if (TextUtils.equals(mimeType, "vnd.android.cursor.item/phone_v2") && TextUtils.equals(data, phoneNumber)) {
                    hasData = true;
                }
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.e(name, "no data");
            hasData = false;
        }
        return hasData;
    }
}
