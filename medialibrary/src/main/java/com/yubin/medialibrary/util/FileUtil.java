package com.yubin.medialibrary.util;

import android.content.Context;
import android.os.Environment;

import com.yubin.baselibrary.util.LogUtil;

import java.io.File;

/**
 * description 开思文件管理类
 *
 * @author yubin
 * @date create at 2020/6/28 16:34
 */
public class FileUtil {

    private final static String EXTERNAL_FILE_NAME = "my_time";

    /**
     * 清空文件夹
     *
     * @param path 路径
     */
    public static void clearFolder(String path) {
        File f = new File(path);
        File[] fs = f.listFiles();
        for (File file : fs) {
            deleteFolder(file.getAbsolutePath());
        }
    }

    /**
     * 删除文件夹
     *
     * @param path 路径
     */
    public static void deleteFolder(String path) {
        File f = new File(path);
        File[] fs = f.listFiles();
        for (File file : fs) {
            if (file.isFile()) {
                if (!file.delete()) {
                    LogUtil.w("delete file " + path + " fail");
                }
            } else {
                deleteFolder(file.getAbsolutePath());
            }
        }
        if (!f.delete()) {
            LogUtil.w("delete file " + path + " fail");
        }
    }

    /**
     * 删除指定文件
     *
     * @param path 文件路径
     * @return
     */
    public static void deleteFile(String path) {
        if (!isStorageMounted()) {
            return;
        }
        if (!new File(path).delete()) {
            LogUtil.w("delete files " + path + " fail");
        }
    }


    /**
     * 获取开思外部文件夹路径
     *
     * @return 返回对应文件夹路径
     */
    public static String getExternalCachePath(Context context, String dirName) {
        File cacheDir = context.getExternalCacheDir();
        String p = cacheDir.getAbsolutePath() + File.separator + File.separator + dirName;
        File pFile = new File(p);
        if (!pFile.exists()) {
            if (!pFile.mkdirs()) {
                LogUtil.w("mkdirs files " + p + " fail");
            }
        }
        return p;
    }

    private static boolean isStorageMounted() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            LogUtil.w("current file state is not mounted");
            return false;
        }
        return true;
    }

}
