package com.getech.imagepicker.bean;

//import android.os.Parcel;
//import android.os.Parcelable;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/24
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ImagePickerResult implements Serializable {
    public ArrayList<HashMap<String, String>> originFiles;
    public ArrayList<HashMap<String, String>> compressFiles;

    public ImagePickerResult(ArrayList<HashMap<String, String>> originFiles, ArrayList<HashMap<String, String>> compressFiles) {
        this.originFiles = originFiles;
        this.compressFiles = compressFiles;
    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(new Gson().toJson(this.originFiles));
//        dest.writeString(new Gson().toJson(this.compressFiles));
//    }
//
//    protected ImagePickerResult(Parcel in) {
//        String originString = in.readString();
//        String compressString = in.readString();
//        this.originFiles = new Gson().fromJson(originString, new TypeToken<HashMap<String, String>>() {
//        }.getType());
//        this.compressFiles = new Gson().fromJson(compressString, new TypeToken<HashMap<String, String>>() {
//        }.getType());
//    }
//
//    public static final Creator<ImagePickerResult> CREATOR = new Creator<ImagePickerResult>() {
//        @Override
//        public ImagePickerResult createFromParcel(Parcel source) {
//            return new ImagePickerResult(source);
//        }
//
//        @Override
//        public ImagePickerResult[] newArray(int size) {
//            return new ImagePickerResult[size];
//        }
//    };
}
