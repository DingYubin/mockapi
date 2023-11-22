package com.yubin.imagepicker;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.yubin.imagepicker.bean.ImageFolder;
import com.yubin.imagepicker.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageDataSource implements LoaderManager.LoaderCallbacks<Cursor> {
    //加载所有图片
    public static final int LOADER_ALL = 0;
    //分类加载图片
    public static final int LOADER_CATEGORY = 1;
    //所有视频
    public static final int LOADER_ALL_VIDEO = 2;
    //分类加载视频
    public static final int LOADER_VIDEO_CATEGORY = 3;
    /**
     * 查询图片需要的数据列
     */
    public static final String[] IMAGE_PROJECTION = {
            //图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DISPLAY_NAME,
            //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.DATA,
            //图片的大小，long型  132492
            MediaStore.Images.Media.SIZE,
            //图片的宽度，int型  1920
            MediaStore.Images.Media.WIDTH,
            //图片的高度，int型  1080
            MediaStore.Images.Media.HEIGHT,
            //图片的类型     image/jpeg
            MediaStore.Images.Media.MIME_TYPE,
            //图片被添加的时间，long型  1450518608
            MediaStore.Images.Media.DATE_ADDED
    };

    /**
     * 查询视频需要的数据列
     */
    public final static String[] VIDEO_PROJECTION = {
            //视频名称
            MediaStore.Video.Media.DISPLAY_NAME,
            //视频路径
            MediaStore.Video.Media.DATA,
            //视频大小的大小，long型  132492
            MediaStore.Video.Media.SIZE,
            //视频宽
            MediaStore.Video.Media.WIDTH,
            //视频搞
            MediaStore.Video.Media.HEIGHT,
            //视频的类型
            MediaStore.Video.Media.MIME_TYPE,
            //视频加入时间
            MediaStore.Video.Media.DATE_ADDED,
            //视频时长
            MediaStore.Video.Media.DURATION,
    };

    private FragmentActivity activity;
    /**
     * 图片加载完成的回调接口
     */
    private OnImagesLoadedListener loadedListener;
    /**
     * 所有的图片文件夹
     */
    private ArrayList<ImageFolder> imageFolders = new ArrayList<>();

    private boolean hasLoad = false;
    /**
     * 是否加载视频
     */
    private boolean loadVideo;
    private int currentLoaderId;

    /**
     * @param activity       用于初始化LoaderManager，需要兼容到2.3
     * @param path           指定扫描的文件夹目录，可以为 null，表示扫描所有图片
     * @param loadedListener 图片加载完成的监听
     */
    public ImageDataSource(FragmentActivity activity, String path, OnImagesLoadedListener loadedListener, boolean loadVideo) {
        this.activity = activity;
        this.loadedListener = loadedListener;
        this.loadVideo = loadVideo;
        LoaderManager loaderManager = activity.getSupportLoaderManager();
        if (loadVideo) {
            if (path == null) {
                //加载所有的视频
                loaderManager.initLoader(LOADER_ALL_VIDEO, null, this);
            } else {
                //加载指定目录的视频
                Bundle bundle = new Bundle();
                bundle.putString("path", path);
                loaderManager.initLoader(LOADER_VIDEO_CATEGORY, bundle, this);
            }
        } else {
            if (path == null) {
                //加载所有的图片
                loaderManager.initLoader(LOADER_ALL, null, this);
            } else {
                //加载指定目录的图片
                Bundle bundle = new Bundle();
                bundle.putString("path", path);
                loaderManager.initLoader(LOADER_CATEGORY, bundle, this);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        currentLoaderId = id;
        //扫描所有图片
        if (id == LOADER_ALL) {
            cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[6] + " DESC");
        }
        //扫描某个图片文件夹
        if (id == LOADER_CATEGORY) {
            cursorLoader = new CursorLoader(activity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[6] + " DESC");
        }
        //扫描所有视频
        if (id == LOADER_ALL_VIDEO) {
            cursorLoader = new CursorLoader(activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION, "duration < " + ImagePicker.getInstance().getMaxDuration() + " and _size < " + ImagePicker.getInstance().getMaxSize(), null, VIDEO_PROJECTION[6] + " DESC");
        }
        //扫描某个视频文件夹
        if (id == LOADER_VIDEO_CATEGORY) {
            cursorLoader = new CursorLoader(activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_PROJECTION, IMAGE_PROJECTION[1] + " like '%" + args.getString("path") + "%' and duration < " + ImagePicker.getInstance().getMaxDuration() + " and _size < " + ImagePicker.getInstance().getMaxSize(), null, VIDEO_PROJECTION[6] + " DESC");
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (hasLoad) {
            return;
        }
        hasLoad = true;
        imageFolders.clear();
        if (data == null) {
            //回调接口，通知图片数据准备完成
            ImagePicker.getInstance().setImageFolders(imageFolders);
            loadedListener.onImagesLoaded(imageFolders);
            return;
        }
        //所有图片的集合,不分文件夹


        if (currentLoaderId == LOADER_ALL || currentLoaderId == LOADER_CATEGORY) {
            imageLoaded(data);
        }
        if (currentLoaderId == LOADER_ALL_VIDEO || currentLoaderId == LOADER_VIDEO_CATEGORY) {
            videoLoaded(data);
        }

        ImagePicker.getInstance().setImageFolders(imageFolders);
        loadedListener.onImagesLoaded(imageFolders);
    }

    private void imageLoaded(Cursor data) {
        ArrayList<ImageItem> allImages = new ArrayList<>();
        while (data.moveToNext()) {
            //查询数据
            String imageName = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
            String imagePath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));

            File file = new File(imagePath);
            if (file == null || !file.exists() || file.length() <= 0) {
                continue;
            }

            long imageSize = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
            int imageWidth = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
            int imageHeight = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
            String imageMimeType = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));
            long imageAddTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));
            //封装实体
            ImageItem imageItem = new ImageItem();
            imageItem.name = imageName;
            imageItem.path = imagePath;
            imageItem.size = imageSize;
            imageItem.width = imageWidth;
            imageItem.height = imageHeight;
            imageItem.mimeType = imageMimeType;
            imageItem.addTime = imageAddTime;
            allImages.add(imageItem);
            //根据父路径分类存放图片
            File imageFile = new File(imagePath);
            File imageParentFile = imageFile.getParentFile();
            ImageFolder imageFolder = new ImageFolder();
            imageFolder.name = imageParentFile.getName();
            imageFolder.path = imageParentFile.getAbsolutePath();

            if (!imageFolders.contains(imageFolder)) {
                ArrayList<ImageItem> images = new ArrayList<>();
                images.add(imageItem);
                imageFolder.cover = imageItem;
                imageFolder.images = images;
                imageFolders.add(imageFolder);
            } else {
                imageFolders.get(imageFolders.indexOf(imageFolder)).images.add(imageItem);
            }
        }
        //防止没有图片报异常
        if (data.getCount() > 0 && allImages.size() > 0) {
            //构造所有图片的集合
            ImageFolder allImagesFolder = new ImageFolder();
            allImagesFolder.name = activity.getResources().getString(R.string.ip_all_images);
            allImagesFolder.path = "/";
            allImagesFolder.cover = allImages.get(0);
            allImagesFolder.images = allImages;
            //确保第一条是所有图片
            imageFolders.add(0, allImagesFolder);
        }
    }

    private void videoLoaded(Cursor data) {
        ArrayList<ImageItem> allVideos = new ArrayList<>();
        while (data.moveToNext()) {
            //查询数据
            String imageName = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));
            String imagePath = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));

            File file = new File(imagePath);
            if (file == null || !file.exists() || file.length() <= 0) {
                continue;
            }

            long imageSize = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
            int imageWidth = data.getInt(data.getColumnIndexOrThrow(VIDEO_PROJECTION[3]));
            int imageHeight = data.getInt(data.getColumnIndexOrThrow(VIDEO_PROJECTION[4]));
            String imageMimeType = data.getString(data.getColumnIndexOrThrow(VIDEO_PROJECTION[5]));
            long imageAddTime = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[6]));
            long duration = data.getLong(data.getColumnIndexOrThrow(VIDEO_PROJECTION[7]));
            //封装实体
            ImageItem imageItem = new ImageItem();
            imageItem.name = imageName;
            imageItem.path = imagePath;
            imageItem.size = imageSize;
            imageItem.width = imageWidth;
            imageItem.height = imageHeight;
            imageItem.mimeType = imageMimeType;
            imageItem.addTime = imageAddTime;
            imageItem.duration = duration;
            imageItem.isVideo = true;
            allVideos.add(imageItem);
            //根据父路径分类存放图片
            File imageFile = new File(imagePath);
            File imageParentFile = imageFile.getParentFile();
            ImageFolder imageFolder = new ImageFolder();
            imageFolder.name = imageParentFile.getName();
            imageFolder.path = imageParentFile.getAbsolutePath();

            if (!imageFolders.contains(imageFolder)) {
                ArrayList<ImageItem> images = new ArrayList<>();
                images.add(imageItem);
                imageFolder.cover = imageItem;
                imageFolder.images = images;
                imageFolders.add(imageFolder);
            } else {
                imageFolders.get(imageFolders.indexOf(imageFolder)).images.add(imageItem);
            }
        }
        //防止没有图片报异常
        if (data.getCount() > 0 && allVideos.size() > 0) {
            //构造所有图片的集合
            ImageFolder allImagesFolder = new ImageFolder();
            allImagesFolder.name = activity.getResources().getString(R.string.ip_all_video);
            allImagesFolder.path = "/";
            allImagesFolder.cover = allVideos.get(0);
            allImagesFolder.images = allVideos;
            //确保第一条是所有图片
            imageFolders.add(0, allImagesFolder);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        System.out.println("--------");
    }

    /**
     * 所有图片加载完成的回调接口
     */
    public interface OnImagesLoadedListener {
        void onImagesLoaded(List<ImageFolder> imageFolders);
    }
}
