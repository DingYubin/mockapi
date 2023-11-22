package com.yubin.imagepicker.bean;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.yubin.imagepicker.ImagePicker;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 图片信息
 */
public class ImageItem implements Serializable, Parcelable {
    /**
     * 图片的名字
     */
    public String name;
    /**
     * 图片的路径
     */
    public String path;
    /**
     * 缩略图的路径
     */
    public String thumbPath;

    /**
     * 图片的大小
     */
    public long size;
    /**
     * 图片的宽度
     */
    public int width;
    /**
     * 图片的高度
     */
    public int height;
    /**
     * 图片的类型
     */
    @SerializedName("type")
    public String mimeType;
    /**
     * 图片的创建时间
     */
    @SerializedName("createDate")
    public long addTime;
    /**
     * 是否为视频
     */
    public boolean isVideo;
    /**
     * 视频时长
     */
    public long duration;

    /**
     * 上传状态 0：成功 -1：失败
     */
    public int uploadStatus = 10010;

    /**
     * 图片的路径和创建时间相同就认为是同一张图片
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof ImageItem) {
            ImageItem item = (ImageItem) o;
            return this.path.equalsIgnoreCase(item.path) && this.addTime == item.addTime;
        }

        return super.equals(o);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.thumbPath);
        dest.writeLong(this.size);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeString(this.mimeType);
        dest.writeLong(this.addTime);
        dest.writeInt(this.isVideo ? 1 : 0);
        dest.writeLong(this.duration);
        dest.writeInt(this.uploadStatus);
    }

    public ImageItem() {
    }

    protected ImageItem(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.thumbPath = in.readString();
        this.size = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.mimeType = in.readString();
        this.addTime = in.readLong();
        this.isVideo = (in.readInt() == 1);
        this.duration = in.readLong();
        this.uploadStatus = in.readInt();
    }

    public static final Parcelable.Creator<ImageItem> CREATOR = new Parcelable.Creator<ImageItem>() {
        @Override
        public ImageItem createFromParcel(Parcel source) {
            return new ImageItem(source);
        }

        @Override
        public ImageItem[] newArray(int size) {
            return new ImageItem[size];
        }
    };

    public HashMap<String, String> toImageHashmap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("width", width + "");
        map.put("height", height + "");
        map.put("createDate", addTime + "");
        map.put("type", mimeType);
        map.put("size", size + "");
        map.put("path", path);
        map.put("uploadStatus", uploadStatus + "");
        return map;
    }

    public HashMap<String, String> toVideoHashmap(Activity activity) {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("width", width + "");
        map.put("height", height + "");
        map.put("createDate", addTime + "");
        map.put("type", mimeType);
        map.put("size", size + "");
        long seconds = duration / 1000;
        if (seconds == 0) {
            seconds = 1;
        }
        map.put("duration", seconds + "");
        map.put("path", path);
        String firstFramePath = ImagePicker.getInstance().getImageLoader().getVideoFirstFrameImagePath(activity, path);
        map.put("firstFramePath", firstFramePath);
        map.put("uploadStatus", uploadStatus + "");
        return map;
    }

}
