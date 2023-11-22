package com.yubin.imagepicker.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.getech.android.httphelper.HttpUtils;
import com.getech.android.httphelper.constant.HttpCacheMode;
import com.getech.android.httphelper.constant.HttpMethodEnum;
import com.getech.android.httphelper.httpinterface.HttpRequestParams;
import com.getech.android.httphelper.httpinterface.rx.RxUploadDownloadResult;
import com.getech.android.utils.FileUtils;
import com.yubin.imagepicker.AudioPlayListener;
import com.yubin.imagepicker.BuildConfig;
import com.yubin.imagepicker.SaveMediaToGalleryListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.DiskCache;
import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.DownloadListener;
import me.panpf.sketch.request.DownloadResult;
import me.panpf.sketch.request.ErrorCause;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class Utils {
    private static IjkMediaPlayer mediaPlayer;
    private static boolean downloadingAudio;
    private static HttpUtils httpUtils;
    private static String httpModuleTag;

    /**
     * 获得状态栏的高度
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 根据屏幕宽度与密度计算GridView显示的列数， 最少为三列，并获取Item宽度
     */
    public static int getImageItemWidth(Activity activity) {
        int screenWidth = activity.getResources().getDisplayMetrics().widthPixels;
//        int densityDpi = activity.getResources().getDisplayMetrics().densityDpi;
//        int cols = screenWidth / densityDpi;
//        cols = cols < 3 ? 3 : cols;
        int cols = 4;
        int columnSpace = (int) (2 * activity.getResources().getDisplayMetrics().density);
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }

    /**
     * 判断SDCard是否可用
     */
    public static boolean existSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机大小（分辨率）
     */
    public static DisplayMetrics getScreenPix(Activity activity) {
        DisplayMetrics displaysMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaysMetrics);
        return displaysMetrics;
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * 判断手机是否含有虚拟按键  99%
     */
    public static boolean hasVirtualNavigationBar(Context context) {
        boolean hasSoftwareKeys = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display d = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }

        return hasSoftwareKeys;
    }

    /**
     * 获取导航栏高度，有些没有虚拟导航栏的手机也能获取到，建议先判断是否有虚拟按键
     */
    public static int getNavigationBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return resourceId > 0 ? context.getResources().getDimensionPixelSize(resourceId) : 0;
    }

    /**
     * 保存bitmap到相册
     *
     * @param context
     * @param bmp         bitmap
     * @param packageName 文件目录
     * @return
     */
    public static String saveImageToPhotoGallery(Context context, Bitmap bmp, String packageName) {
        // 保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), packageName == null ? "" : packageName);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Throwable e) {
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                if (BuildConfig.DEBUG) {
                    Log.d("ImageUtils", "path: " + path + " uri: " + uri);
                }
            }
        });
        return file.getAbsolutePath();
    }

    /**
     * 保存图片文件到到相册
     *
     * @param context
     * @param needSaveFilePath 要保存的文件
     * @param packageName      文件目录
     * @throws FileNotFoundException
     */
    public static String saveImageToPhotoGallery(Context context, String needSaveFilePath, final String packageName) throws FileNotFoundException {
        File appDir = new File(Environment.getExternalStorageDirectory(), packageName == null ? "" : packageName);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String ext = ".jpg";
        if (needSaveFilePath.endsWith(".gif")) {
            ext = ".gif";
        }
        String fileName = System.currentTimeMillis() + ext;
        File file = new File(appDir, fileName);
        boolean copySuccess = FileUtils.copyFile(needSaveFilePath, file.getAbsolutePath());
        if (!copySuccess) {
            return "";
        }

        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                if (BuildConfig.DEBUG) {
                    Log.d("ImageUtils", "path: " + path + " uri: " + uri);
                }
            }
        });
        return file.getAbsolutePath();
    }

    public static boolean isImageInPhotoGallery(Context context, String imagePath) {
        if (TextUtils.isEmpty(imagePath) || imagePath.startsWith("http")) {
            return false;
        }
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, MediaStore.Images.Media.DATA + " = \'" + imagePath + "\'", null, null);
        if (cursor == null || cursor.getCount() <= 0) {
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * 下载网络图片，并保存到相册中
     *
     * @param context
     * @param uri
     */
    public static void downloadImageAndSaveToGallery(final Context context, final String uri, final String packageName, final SaveMediaToGalleryListener listener) {
        if (TextUtils.isEmpty(uri)) {
            if (listener != null) {
                listener.onError(uri);
            }
            return;
        }
        if (!uri.startsWith("http")) {
            try {
                if (listener != null) {
                    listener.onStart(uri);
                }
                File localFile = new File(uri);
                if (localFile == null || !localFile.exists() || localFile.length() < 10) {
                    if (listener != null) {
                        listener.onError(uri);
                    }
                    return;
                }
                String savedFilePath = saveImageToPhotoGallery(context, uri, packageName == null ? "" : packageName);
                if (listener != null) {
                    if (!TextUtils.isEmpty(savedFilePath)) {
                        listener.onComplete(uri);
                    } else {
                        listener.onError(uri);
                    }
                }
            } catch (Throwable e) {
                if (listener != null) {
                    listener.onError(uri);
                }
            }
            return;
        }
        Sketch.with(context)
                .download(uri, new DownloadListener() {
                    @Override
                    public void onStarted() {
                        if (listener != null) {
                            listener.onStart(uri);
                        }
                    }

                    @Override
                    public void onError(@NonNull ErrorCause cause) {
                        if (listener != null) {
                            listener.onError(uri);
                        }
                    }

                    @Override
                    public void onCanceled(@NonNull CancelCause cause) {
                        if (listener != null) {
                            listener.onError(uri);
                        }
                    }

                    @Override
                    public void onCompleted(DownloadResult downloadResult) {
                        DiskCache.Entry cache = downloadResult.getDiskCacheEntry();
                        File imageFile = cache.getFile();
                        try {
                            saveImageToPhotoGallery(context, imageFile.getAbsolutePath(), packageName);
                        } catch (Throwable e) {
                            if (listener != null) {
                                listener.onError(uri);
                            }
                        }
                        if (listener != null) {
                            listener.onComplete(uri);
                        }
                    }
                })
                .commit();
    }

    /**
     * 保存视频到相册
     *
     * @param context
     * @param needSaveFilePath 要保存的视频地址
     * @param listener         保存监听器
     */
    public static void saveVideoToPhotoGallery(Context context, final String needSaveFilePath, final SaveMediaToGalleryListener listener) {
        if (listener != null) {
            listener.onError(needSaveFilePath);
        }
        File localFile = new File(needSaveFilePath);
        if (localFile == null || !localFile.exists() || localFile.length() < 10) {
            if (listener != null) {
                listener.onError(needSaveFilePath);
            }
            return;
        }
        MediaScannerConnection.scanFile(context, new String[]{needSaveFilePath}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                if (BuildConfig.DEBUG) {
                    Log.d("ImageUtils", "path: " + path + " uri: " + uri);
                }
                if (listener != null) {
                    listener.onComplete(needSaveFilePath);
                }
            }
        });
    }

    /**
     * 播放音频
     *
     * @param activity
     * @param audioUrl
     */
    public static void playAudio(Activity activity, final String audioUrl, AudioPlayListener listener) {
        if (TextUtils.isEmpty(audioUrl)) {
            if (listener == null) {
                listener.onError(audioUrl, AudioPlayListener.PLAY_ERROR_EMPTY_URL);
            }
            return;
        }
        if ((mediaPlayer != null && mediaPlayer.isPlaying()) || downloadingAudio) {
            if (listener != null) {
                listener.onError(audioUrl, AudioPlayListener.PLAY_ERROR_IS_PLAYING);
            }
            return;
        }

        if (!audioUrl.startsWith("http")) {
            playLocalAudio(audioUrl, audioUrl, listener);
            return;
        }
        playRemoteAudio(activity, audioUrl, listener);
    }

    /**
     * 播放本地音频
     *
     * @param audioPath
     * @param listener
     */
    private static void playLocalAudio(String audioPath, String audioUrl, AudioPlayListener listener) {
        File localFile = new File(audioPath);
        if (localFile == null || !localFile.exists() || localFile.length() < 10) {
            if (listener != null) {
                listener.onError(audioUrl, AudioPlayListener.PLAY_ERROR_NOT_AUDIO);
            }
            return;
        }
        if (listener != null) {
            listener.onStart(audioUrl);
        }
        //播放本地音频
        resetMediaPlayer();
        preparePlayAudio(audioPath, audioUrl, listener);
    }

    /**
     * 播放远程音频
     *
     * @param activity
     * @param audioUrl
     * @param listener
     */
    private static void playRemoteAudio(Activity activity, String audioUrl, AudioPlayListener listener) {
        downloadingAudio = true;
        httpUtils = HttpUtils.init(activity.getApplication())
                .commonTimeout(60)
                .setCacheMode(HttpCacheMode.NO_CACHE)
                .showLog(true)
                .setCacheTime(10000)
                .setRetryCount(3);
        httpModuleTag = httpUtils.getModuleTag();
        String ext = ".mp3";
        int lastIndex = audioUrl.lastIndexOf(".");
        if (lastIndex > 0) {
            String tempExt = audioUrl.substring(lastIndex + 1);
            if (!TextUtils.isEmpty(tempExt)) {
                ext = "." + tempExt;
            }
        }
        String fileName = System.currentTimeMillis() + ext;
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + fileName;
        HttpRequestParams httpRequestParams = new HttpRequestParams()
                .setHttpMethod(HttpMethodEnum.GET)
                .setUrl(audioUrl)
                .setFileDownloadPath(filePath)
                .setTimeout(80);
        HttpUtils.getInstance(httpModuleTag)
                .rxDownload(httpRequestParams)
                .subscribe(result -> {
                    if (result.status == RxUploadDownloadResult.UPLOAD_DOWNLOAD_STATUS_FINISHED) {
                        File file = result.response;
                        if (file == null) {
                            if (listener != null) {
                                listener.onError(audioUrl, AudioPlayListener.PLAY_ERROR_DOWNLOAD_FAILED);
                            }
                            return;
                        }
                        downloadingAudio = false;
                        //播放本地音频文件
                        playLocalAudio(file.getAbsolutePath(), audioUrl, listener);
                    }
                }, throwable -> {
                    downloadingAudio = false;
                    if (listener != null) {
                        listener.onError(audioUrl, AudioPlayListener.PLAY_ERROR_DOWNLOAD_FAILED);
                    }
                }, () -> {
                    downloadingAudio = false;
                });
    }

    private static void resetMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private static void preparePlayAudio(final String audioFilePath, String audioUrl, final AudioPlayListener listener) {
        try {
            mediaPlayer = new IjkMediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(IMediaPlayer iMediaPlayer) {
                    if (listener != null) {
                        listener.onComplete(audioUrl);
                    }
                    File tempFile = new File(audioFilePath);
                    if (tempFile != null && tempFile.exists() && audioUrl.startsWith("http")) {
                        tempFile.delete();
                    }
                    removeHttpUtils();
                }
            });
            mediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                    if (listener != null) {
                        listener.onError(audioUrl, AudioPlayListener.PLAY_ERROR_PLAY);
                    }
                    File tempFile = new File(audioFilePath);
                    if (tempFile != null && tempFile.exists() && audioUrl.startsWith("http")) {
                        tempFile.delete();
                    }
                    removeHttpUtils();
                    return false;
                }
            });
            mediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(IMediaPlayer iMediaPlayer) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            if (listener != null) {
                listener.onError(audioFilePath, AudioPlayListener.PLAY_ERROR_PLAY);
            }
            removeHttpUtils();
        }
    }

    /**
     * 停止音频播放
     */
    public static void stopAudioPlay() {
        resetMediaPlayer();
        if (httpUtils != null) {
            httpUtils.cancelAll();
        }
        removeHttpUtils();
    }

    private static void removeHttpUtils() {
        if (httpModuleTag != null) {
            HttpUtils.removeHtttpUtils(httpModuleTag);
            httpModuleTag = null;
        }
    }

    /**
     * 获取当前app版本号
     *
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
}
