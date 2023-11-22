package com.getech.android.httphelper.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.Random;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DownloadUtil {
    /**
     * 获取文件临时保存目录
     *
     * @param context
     * @return
     */
    public static String getTempCacheFolder(Context context) {
        String downloadFolder = Environment.getExternalStorageDirectory().getPath() + "/" + (context == null ? "" : context.getPackageName()) + "/download/temp/";
        File folder = new File(downloadFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return downloadFolder;
    }

    /**
     * 获取临时缓存文件名称
     *
     * @return
     */
    public static String getTempCacheFileName() {
        return System.currentTimeMillis() + "" + (new Random().nextInt(10000));
    }
}
