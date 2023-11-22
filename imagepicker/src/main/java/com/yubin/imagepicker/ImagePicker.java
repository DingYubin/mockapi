package com.yubin.imagepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.yubin.imagepicker.bean.ImageFolder;
import com.yubin.imagepicker.bean.ImageItem;
import com.yubin.imagepicker.loader.ImageLoader;
import com.yubin.imagepicker.ui.ImageGridActivity;
import com.yubin.imagepicker.ui.ImageVideoPlayActivity;
import com.yubin.imagepicker.ui.RemoteImagePreviewActivity;
import com.yubin.imagepicker.util.ProviderUtil;
import com.yubin.imagepicker.util.Utils;
import com.yubin.imagepicker.view.CropImageView;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

/**
 * 采用单例和弱引用解决Intent传值限制导致的异常
 */
public class ImagePicker {

    public static final String TAG = ImagePicker.class.getSimpleName();
    public static final int REQUEST_CODE_TAKE = 1001;
    public static final int REQUEST_CODE_CROP = 1002;
    public static final int REQUEST_CODE_PREVIEW = 1003;
    public static final int RESULT_CODE_ITEMS = 1004;
    public static final int RESULT_CODE_BACK = 1005;

    public static final String EXTRA_RESULT_ITEMS = "extra_result_items";
    public static final String EXTRA_SELECTED_IMAGE_POSITION = "selected_image_position";
    public static final String EXTRA_IMAGE_ITEMS = "extra_image_items";
    public static final String EXTRA_FROM_ITEMS = "extra_from_items";
    public static final String EXTRA_RESULT_COMPRESS = "extra_result_compress";
    /**
     * 返回带压缩图片数据
     */
    public static final String EXTRA_RESULT_MEDIA_ITEMS = "extra_result_media_items";
    /**
     * 返回视频压缩数据
     */
    public static final String EXTRA_RESULT_VIDEO_ITEMS = "extra_result_video_items";
    /**
     * 预览的远程图片地址数组
     */
    public static final String EXTRA_REMOTE_PREVIEW_URLS = "extra_remote_preview_urls";
    /**
     * 预览的远程图片第一张图片索引
     */
    public static final String EXTRA_REMOTE_PREVIEW_FIRST_POSITION = "extra_remote_preview_first_position";
    /**
     * 预览的远程图片是否可以保存
     */
    public static final String EXTRA_REMOTE_PREVIEW_CAN_SAVE = "extra_remote_preview_can_save";

    /**
     * 默认最大视频长度
     */
    public static final long DEFAULT_MAX_VIDEO_DURATION = 31 * 1000L;
    /**
     * 默认最大视频size
     */
    public static final long DEFAULT_MAX_VIDEO_SIZE = 50 * 1024 * 1024L;

    private boolean multiMode = true;    //图片选择模式
    private int selectLimit = 9;         //最大选择图片数量
    private boolean crop = true;         //裁剪
    private boolean showCamera = true;   //显示相机
    private boolean isSaveRectangle = false;  //裁剪后的图片是否是矩形，否者跟随裁剪框的形状
    private int outPutX = 800;           //裁剪保存宽度
    private int outPutY = 800;           //裁剪保存高度
    private int focusWidth = 280;         //焦点框的宽度
    private int focusHeight = 280;        //焦点框的高度
    private ImageLoader imageLoader;     //图片加载器
    private CropImageView.Style style = CropImageView.Style.RECTANGLE; //裁剪框的形状
    private File cropCacheFolder;
    private File takeImageFile;
    public Bitmap cropBitmap;
    private boolean showOrigin = true;
    private boolean selectVideo;
    /**
     * 视频最长时长，默认30秒
     */
    private long maxDuration = DEFAULT_MAX_VIDEO_DURATION;
    /**
     * 视频最大size，默认50M
     */
    private long maxSize = DEFAULT_MAX_VIDEO_SIZE;
    /**
     * 是否压缩图片
     */
    private boolean compressImage;
    /**
     * 是否为预览图片
     */
    private boolean showPreview;
    /**
     * 预览开始索引
     */
    private int previewIndex = 0;
    /**
     * 预览数组
     */
    private ArrayList<String> previewUrls;

    private ArrayList<ImageItem> mSelectedImages = new ArrayList<>();   //选中的图片集合
    private List<ImageFolder> mImageFolders;      //所有的图片文件夹
    private int mCurrentImageFolderPosition = 0;  //当前选中的文件夹位置 0表示所有图片
    private List<OnImageSelectedListener> mImageSelectedListeners;          // 图片选中的监听回调

    private static ImagePicker mInstance;
    private String httpModuleTag;

    private ImagePicker() {
    }

    public static ImagePicker getInstance() {
        if (mInstance == null) {
            synchronized (ImagePicker.class) {
                if (mInstance == null) {
                    mInstance = new ImagePicker();
                }
            }
        }
        return mInstance;
    }

    public boolean isMultiMode() {
        return multiMode;
    }

    public void setMultiMode(boolean multiMode) {
        this.multiMode = multiMode;
    }

    public int getSelectLimit() {
        return selectLimit;
    }

    public void setSelectLimit(int selectLimit) {
        this.selectLimit = selectLimit;
    }

    public boolean isCrop() {
        return crop;
    }

    public void setCrop(boolean crop) {
        this.crop = crop;
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public boolean isSaveRectangle() {
        return isSaveRectangle;
    }

    public void setSaveRectangle(boolean isSaveRectangle) {
        this.isSaveRectangle = isSaveRectangle;
    }

    public int getOutPutX() {
        return outPutX;
    }

    public void setOutPutX(int outPutX) {
        this.outPutX = outPutX;
    }

    public int getOutPutY() {
        return outPutY;
    }

    public void setOutPutY(int outPutY) {
        this.outPutY = outPutY;
    }

    public int getFocusWidth() {
        return focusWidth;
    }

    public void setFocusWidth(int focusWidth) {
        this.focusWidth = focusWidth;
    }

    public int getFocusHeight() {
        return focusHeight;
    }

    public void setFocusHeight(int focusHeight) {
        this.focusHeight = focusHeight;
    }

    public File getTakeImageFile() {
        return takeImageFile;
    }

    public File getCropCacheFolder(Context context) {
        if (cropCacheFolder == null) {
            cropCacheFolder = new File(context.getCacheDir() + "/ImagePicker/cropTemp/");
        }
        return cropCacheFolder;
    }

    public void setCropCacheFolder(File cropCacheFolder) {
        this.cropCacheFolder = cropCacheFolder;
    }

    public boolean isShowOrigin() {
        return showOrigin;
    }

    public void setShowOrigin(boolean showOrigin) {
        this.showOrigin = showOrigin;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public CropImageView.Style getStyle() {
        return style;
    }

    public void setStyle(CropImageView.Style style) {
        this.style = style;
    }

    public List<ImageFolder> getImageFolders() {
        return mImageFolders;
    }

    public void setImageFolders(List<ImageFolder> imageFolders) {
        mImageFolders = imageFolders;
    }

    public int getCurrentImageFolderPosition() {
        return mCurrentImageFolderPosition;
    }

    public void setCurrentImageFolderPosition(int mCurrentSelectedImageSetPosition) {
        mCurrentImageFolderPosition = mCurrentSelectedImageSetPosition;
    }

    public ArrayList<ImageItem> getCurrentImageFolderItems() {
        return mImageFolders.get(mCurrentImageFolderPosition).images;
    }

    public boolean isSelect(ImageItem item) {
        return mSelectedImages.contains(item);
    }

    public boolean isSelectVideo() {
        return selectVideo;
    }

    public void setSelectVideo(boolean selectVideo) {
        this.selectVideo = selectVideo;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public boolean isCompressImage() {
        return compressImage;
    }

    public void setCompressImage(boolean compressImage) {
        this.compressImage = compressImage;
    }

    public int getSelectImageCount() {
        if (mSelectedImages == null) {
            return 0;
        }
        return mSelectedImages.size();
    }

    public boolean isShowPreview() {
        return showPreview;
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
    }

    public int getPreviewIndex() {
        return previewIndex;
    }

    public void setPreviewIndex(int previewIndex) {
        this.previewIndex = previewIndex;
    }

    public ArrayList<String> getPreviewUrls() {
        return previewUrls;
    }

    public void setPreviewUrls(ArrayList<String> previewUrls) {
        this.previewUrls = previewUrls;
    }

    public ArrayList<ImageItem> getSelectedImages() {
        return mSelectedImages;
    }

    public void clearSelectedImages() {
        if (mSelectedImages != null) {
            mSelectedImages.clear();
        }
    }

    public void clear() {
        if (mImageSelectedListeners != null) {
            mImageSelectedListeners.clear();
            mImageSelectedListeners = null;
        }
        if (mImageFolders != null) {
            mImageFolders.clear();
            mImageFolders = null;
        }
        if (mSelectedImages != null) {
            mSelectedImages.clear();
        }
        mCurrentImageFolderPosition = 0;
    }

    /**
     * 拍照的方法
     */
    public void takePicture(Activity activity, int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            if (Utils.existSDCard()) {
                takeImageFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/camera/");
            } else {
                takeImageFile = Environment.getDataDirectory();
            }
            takeImageFile = createFile(takeImageFile, "IMG_", ".jpg");
            if (takeImageFile != null) {
                // 默认情况下，即不需要指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。如果想访问原始图片，
                // 可以通过dat extra能够得到原始图片位置。即，如果指定了目标uri，data就没有数据，
                // 如果没有指定uri，则data就返回有数据！

                Uri uri;
                if (VERSION.SDK_INT <= VERSION_CODES.M) {
                    uri = Uri.fromFile(takeImageFile);
                } else {
                    /**
                     * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
                     * 并且这样可以解决MIUI系统上拍照返回size为0的情况
                     */
                    uri = FileProvider.getUriForFile(activity, ProviderUtil.getFileProviderName(activity), takeImageFile);
                    //加入uri权限 要不三星手机不能拍照
                    List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        activity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                }

                Log.e("nanchen", ProviderUtil.getFileProviderName(activity));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
        }
        activity.startActivityForResult(takePictureIntent, requestCode);
    }

    /**
     * 根据系统时间、前缀、后缀产生一个文件
     */
    public static File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    /**
     * 扫描图片
     */
    public static void galleryAddPic(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * 图片选中的监听
     */
    public interface OnImageSelectedListener {
        void onImageSelected(int position, ImageItem item, boolean isAdd);
    }

    public void addOnImageSelectedListener(OnImageSelectedListener l) {
        if (mImageSelectedListeners == null) {
            mImageSelectedListeners = new ArrayList<>();
        }
        mImageSelectedListeners.add(l);
    }

    public void removeOnImageSelectedListener(OnImageSelectedListener l) {
        if (mImageSelectedListeners == null) {
            return;
        }
        mImageSelectedListeners.remove(l);
    }

    public void addSelectedImageItem(int position, ImageItem item, boolean isAdd) {
        if (isAdd) {
            mSelectedImages.add(item);
        } else {
            mSelectedImages.remove(item);
        }
        notifyImageSelectedChanged(position, item, isAdd);
    }

    public void setSelectedImages(ArrayList<ImageItem> selectedImages) {
        if (selectedImages == null) {
            return;
        }
        this.mSelectedImages = selectedImages;
    }

    private void notifyImageSelectedChanged(int position, ImageItem item, boolean isAdd) {
        if (mImageSelectedListeners == null) {
            return;
        }
        for (OnImageSelectedListener l : mImageSelectedListeners) {
            l.onImageSelected(position, item, isAdd);
        }
    }

    /**
     * 用于手机内存不足，进程被系统回收，重启时的状态恢复
     */
    public void restoreInstanceState(Bundle savedInstanceState) {
        cropCacheFolder = (File) savedInstanceState.getSerializable("cropCacheFolder");
        takeImageFile = (File) savedInstanceState.getSerializable("takeImageFile");
        imageLoader = (ImageLoader) savedInstanceState.getSerializable("imageLoader");
        style = (CropImageView.Style) savedInstanceState.getSerializable("style");
        multiMode = savedInstanceState.getBoolean("multiMode");
        crop = savedInstanceState.getBoolean("crop");
        showCamera = savedInstanceState.getBoolean("showCamera");
        isSaveRectangle = savedInstanceState.getBoolean("isSaveRectangle");
        selectLimit = savedInstanceState.getInt("selectLimit");
        outPutX = savedInstanceState.getInt("outPutX");
        outPutY = savedInstanceState.getInt("outPutY");
        focusWidth = savedInstanceState.getInt("focusWidth");
        focusHeight = savedInstanceState.getInt("focusHeight");
    }

    /**
     * 用于手机内存不足，进程被系统回收时的状态保存
     */
    public void saveInstanceState(Bundle outState) {
        outState.putSerializable("cropCacheFolder", cropCacheFolder);
        outState.putSerializable("takeImageFile", takeImageFile);
        outState.putSerializable("imageLoader", imageLoader);
        outState.putSerializable("style", style);
        outState.putBoolean("multiMode", multiMode);
        outState.putBoolean("crop", crop);
        outState.putBoolean("showCamera", showCamera);
        outState.putBoolean("isSaveRectangle", isSaveRectangle);
        outState.putInt("selectLimit", selectLimit);
        outState.putInt("outPutX", outPutX);
        outState.putInt("outPutY", outPutY);
        outState.putInt("focusWidth", focusWidth);
        outState.putInt("focusHeight", focusHeight);
    }

    public ImageItem getImageInfoByPath(Context context, String imageFilePath) {
        ImageItem imageItem = new ImageItem();
        File file = new File(imageFilePath);
        if (file == null || !file.exists()) {
            imageItem.path = imageFilePath;
            return imageItem;
        }
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ImageDataSource.IMAGE_PROJECTION, MediaStore.Images.Media.DATA + "=? ", new String[]{imageFilePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            String imageName = cursor.getString(cursor.getColumnIndexOrThrow(ImageDataSource.IMAGE_PROJECTION[0]));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(ImageDataSource.IMAGE_PROJECTION[1]));
            long imageSize = cursor.getLong(cursor.getColumnIndexOrThrow(ImageDataSource.IMAGE_PROJECTION[2]));
            int imageWidth = cursor.getInt(cursor.getColumnIndexOrThrow(ImageDataSource.IMAGE_PROJECTION[3]));
            int imageHeight = cursor.getInt(cursor.getColumnIndexOrThrow(ImageDataSource.IMAGE_PROJECTION[4]));
            String imageMimeType = cursor.getString(cursor.getColumnIndexOrThrow(ImageDataSource.IMAGE_PROJECTION[5]));
            long imageAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(ImageDataSource.IMAGE_PROJECTION[6]));
            //封装实体
            imageItem.name = imageName;
            imageItem.path = imagePath;
            imageItem.size = imageSize;
            imageItem.width = imageWidth;
            imageItem.height = imageHeight;
            imageItem.mimeType = imageMimeType;
            imageItem.addTime = imageAddTime;
        } else {
            //通过exif文件获取
            try {
                ExifInterface exifInterface = new ExifInterface(imageFilePath);
                imageItem.path = imageFilePath;
                int lastIndex = imageFilePath.lastIndexOf("/");
                imageItem.name = imageFilePath.substring(lastIndex + 1);
                imageItem.size = file.length();
                imageItem.width = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
                imageItem.height = exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
                imageItem.addTime = getDateTime(exifInterface);
                imageItem.mimeType = "image/jpeg";
            } catch (Throwable e) {
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return imageItem;
    }

    private static final Pattern sNonZeroTimePattern = Pattern.compile(".*[1-9].*");
    private static SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

    private long getDateTime(ExifInterface exifInterface) {
        sFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateTimeString = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        if (dateTimeString == null
                || !sNonZeroTimePattern.matcher(dateTimeString).matches()) {
            return -1;
        }

        ParsePosition pos = new ParsePosition(0);
        try {
            // The exif field is in local time. Parsing it as if it is UTC will yield time
            // since 1/1/1970 local time
            Date datetime = sFormatter.parse(dateTimeString, pos);
            if (datetime == null) {
                return -1;
            }
            long msecs = datetime.getTime();
            if (VERSION.SDK_INT >= 23) {
                String subSecs = exifInterface.getAttribute(ExifInterface.TAG_SUBSEC_TIME);
                if (subSecs != null) {
                    try {
                        long sub = Long.parseLong(subSecs);
                        while (sub > 1000) {
                            sub /= 10;
                        }
                        msecs += sub;
                    } catch (NumberFormatException e) {
                        // Ignored
                    }
                }
            }
            return msecs;
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    /**
     * 拍照获取图片
     *
     * @param activity
     * @param requestCode
     * @param imageLoader
     */
    public static void getPictureFromCamera(Activity activity, int requestCode, ImageLoader imageLoader) {
        if (activity == null || imageLoader == null) {
            return;
        }
        getPictureFromCamera(activity, requestCode, false, imageLoader);
    }

    /**
     * 拍照获取图片
     *
     * @param activity
     * @param requestCode
     * @param compressImage
     * @param imageLoader
     */
    public static void getPictureFromCamera(Activity activity, int requestCode, boolean compressImage, ImageLoader imageLoader) {
        if (activity == null || imageLoader == null) {
            return;
        }
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(imageLoader);
        imagePicker.setShowCamera(true);
        imagePicker.setMultiMode(false);
        imagePicker.setCrop(false);
        imagePicker.setSaveRectangle(false);
        imagePicker.setSelectLimit(1);
        imagePicker.setSelectVideo(false);
        imagePicker.setCompressImage(compressImage);
        Intent intent = new Intent(activity, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 从相册获取照片
     *
     * @param activity
     * @param requestCode
     * @param max
     * @param showOrigin
     * @param imageLoader
     */
    public static void getPictureFromGallery(Activity activity, int requestCode, int max, boolean showOrigin, ImageLoader imageLoader) {
        if (activity == null || imageLoader == null) {
            return;
        }
        getPictureFromGallery(activity, requestCode, max, showOrigin, false, imageLoader);
    }

    /**
     * 从相册获取照片
     *
     * @param activity
     * @param requestCode
     * @param max
     * @param showOrigin
     * @param compressImage 是否需要压缩图片
     * @param imageLoader
     */
    public static void getPictureFromGallery(Activity activity, int requestCode, int max, boolean showOrigin, boolean compressImage, ImageLoader imageLoader) {
        if (activity == null || imageLoader == null) {
            return;
        }
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(imageLoader);
        imagePicker.setShowCamera(false);
        imagePicker.setCrop(false);
        imagePicker.setSaveRectangle(false);
        imagePicker.setMultiMode(max > 1);
        imagePicker.setSelectLimit(max);
        imagePicker.setShowOrigin(showOrigin);
        imagePicker.setSelectVideo(false);
        imagePicker.setCompressImage(compressImage);
        Intent intent = new Intent(activity, ImageGridActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 从相册和相机获取
     *
     * @param activity
     * @param requestCode
     * @param max
     * @param showOrigin
     * @param imageLoader
     */
    public static void getPictureFromCameraAndGallery(Activity activity, int requestCode, int max, boolean showOrigin, ImageLoader imageLoader) {
        if (activity == null || imageLoader == null) {
            return;
        }
        getPictureFromCameraAndGallery(activity, requestCode, max, showOrigin, false, imageLoader);
    }

    /**
     * 从相册和相机获取
     *
     * @param activity
     * @param requestCode
     * @param max
     * @param showOrigin    是否显示选择原图
     * @param compressImage 是否压缩图片
     * @param imageLoader
     */
    public static void getPictureFromCameraAndGallery(Activity activity, int requestCode, int max, boolean showOrigin, boolean compressImage, ImageLoader imageLoader) {
        if (activity == null || imageLoader == null) {
            return;
        }
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(imageLoader);
        imagePicker.setShowCamera(true);
        imagePicker.setCrop(false);
        imagePicker.setSaveRectangle(false);
        imagePicker.setMultiMode(max > 1);
        imagePicker.setSelectLimit(max);
        imagePicker.setShowOrigin(showOrigin);
        imagePicker.setSelectVideo(false);
        imagePicker.setCompressImage(compressImage);
        Intent intent = new Intent(activity, ImageGridActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 选择视频
     *
     * @param activity
     * @param requestCode
     * @param max         最多选择个数
     * @param imageLoader
     */
    public static void getVideos(Activity activity, int requestCode, int max, ImageLoader imageLoader) {
        if (activity == null || imageLoader == null) {
            return;
        }
        getVideos(activity, requestCode, max, DEFAULT_MAX_VIDEO_DURATION, DEFAULT_MAX_VIDEO_SIZE, imageLoader);
    }

    /**
     * 选择视频
     *
     * @param activity
     * @param requestCode
     * @param max         最多选择个数
     * @param maxDuration 最大视频时长，单位：毫秒
     * @param imageLoader
     */
    public static void getVideos(Activity activity, int requestCode, int max, long maxDuration, ImageLoader imageLoader) {
        getVideos(activity, requestCode, max, maxDuration, DEFAULT_MAX_VIDEO_SIZE, imageLoader);
    }

    /**
     * 选择视频
     *
     * @param activity
     * @param requestCode
     * @param max         最多选择个数
     * @param maxDuration 最大视频时长，单位：毫秒
     * @param maxSize     最大视频size，单位：字节
     * @param imageLoader
     */
    public static void getVideos(Activity activity, int requestCode, int max, long maxDuration, long maxSize, ImageLoader imageLoader) {
        if (activity == null || imageLoader == null) {
            return;
        }
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(imageLoader);
        imagePicker.setMultiMode(max > 1);
        imagePicker.setShowCamera(false);
        imagePicker.setSelectLimit(max);
        imagePicker.setShowOrigin(false);
        imagePicker.setSelectVideo(true);
        imagePicker.setMaxDuration(maxDuration);
        imagePicker.setMaxSize(maxSize);
        Intent intent = new Intent(activity, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_VIDEOS, true);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 播放视频
     *
     * @param activity  activity
     * @param videoPath 播放的视频地址，url
     * @param videoName 视频的名称
     */
    public static void playVideo(Activity activity, String videoPath, String videoName) {
        if (activity == null || TextUtils.isEmpty(videoPath)) {
            return;
        }
        playVideo(activity, videoPath, videoName, null);
    }

    /**
     * 播放视频
     *
     * @param activity    activity
     * @param videoPath   播放的视频地址，url
     * @param videoName   视频的名称
     * @param imageLoader 图片加载器
     */
    public static void playVideo(Activity activity, String videoPath, String videoName, ImageLoader imageLoader) {
        if (activity == null || TextUtils.isEmpty(videoPath)) {
            return;
        }
        if (imageLoader != null) {
            ImagePicker imagePicker = ImagePicker.getInstance();
            imagePicker.setImageLoader(imageLoader);
        }
        Intent intent = new Intent(activity, ImageVideoPlayActivity.class);
        Bundle data = new Bundle();
        data.putString("video_path", videoPath);
        data.putString("video_name", videoName);
        intent.putExtras(data);
        activity.startActivity(intent);
    }

    /**
     * 预览远程图片
     *
     * @param activity activity
     * @param index    从第几张图片开始，第一张传0,
     * @param urls     要预览的图片的url数组，可以是本地地址，远程地址
     * @param canSave  是否支持保存
     */
    public static void previewImages(Activity activity, int index, ArrayList<String> urls, boolean canSave) {
        if (activity == null) {
            return;
        }
        if (urls == null || urls.size() == 0) {
            return;
        }
        if (index < 0 || index > urls.size()) {
            index = 0;
        }
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setShowPreview(true);
        Intent intent = new Intent(activity, RemoteImagePreviewActivity.class);
        Bundle data = new Bundle();
        data.putStringArrayList(EXTRA_REMOTE_PREVIEW_URLS, urls);
        data.putInt(EXTRA_REMOTE_PREVIEW_FIRST_POSITION, index);
        data.putBoolean(EXTRA_REMOTE_PREVIEW_CAN_SAVE, canSave);
        intent.putExtras(data);
        activity.startActivity(intent);
    }

    /**
     * 预览远程图片
     *
     * @param activity activity
     * @param index    从第几张图片开始，第一张传0,
     * @param urls     要预览的图片的url数组，可以是本地地址，远程地址
     */
    public static void previewImages(Activity activity, int index, ArrayList<String> urls) {
        previewImages(activity, index, urls, false);
    }

    /**
     * 保存视频到相册
     *
     * @param context
     * @param needSaveFilePath 要保存的视频地址
     * @param listener         保存监听器
     */
    public static void saveVideoToPhotoGallery(Context context, final String needSaveFilePath, final SaveMediaToGalleryListener listener) {
        Utils.saveVideoToPhotoGallery(context, needSaveFilePath, listener);
    }

    /**
     * 下载网络图片，并保存到相册中
     *
     * @param context
     * @param uri
     */
    public static void downloadImageAndSaveToGallery(final Context context, final String uri, final String packageName, final SaveMediaToGalleryListener listener) {
        Utils.downloadImageAndSaveToGallery(context, uri, packageName, listener);
    }

    /**
     * 播放音频
     *
     * @param activity
     * @param audioUrl
     * @param listener
     */
    public static void playAudio(Activity activity, String audioUrl, AudioPlayListener listener) {
        Utils.playAudio(activity, audioUrl, listener);
    }

    /**
     * 停止音频播放
     * 停止音频播放
     */
    public static void stopAudioPlay() {
        Utils.stopAudioPlay();
    }
}