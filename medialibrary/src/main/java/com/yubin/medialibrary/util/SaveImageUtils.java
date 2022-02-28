package com.yubin.medialibrary.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveImageUtils {
    // 将图片保存到应用当中然后传递图片的uri给RN端（RN端的Image只能识别原生传过来的Base64或者uri数据）
    public static String saveImageUri(Bitmap bitmap, Context context, String imageFile) {
        if (bitmap == null) {
            return "";
        }
        String imagePath;
        if (TextUtils.isEmpty(imageFile)) {
            imagePath = pictureVinName(context, false, null);
        } else {
            imagePath = pictureVinName(context, true, imageFile);
        }
        //todo sdk version >23 时需要动态获取运行时权限
        File file = new File(imagePath);
        if (file.exists()) {
            boolean ret = file.delete();
            if (!ret) {
                LogUtil.e("delete file fail");
            }
        }
        try {
            boolean ret = file.createNewFile();
            if (!ret) {
                LogUtil.e("create new file fail");
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            Toast.makeText(context, "图像存储失败", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            LogUtil.d(e.getMessage());
        }
        return imagePath;
    }
    public static String pictureVinName(@NonNull Context context, boolean success, String vinCode) {
        File vinFile = context.getExternalFilesDir("vinCode");
        String prefix;
        if (null != vinFile) {
            prefix = vinFile.getAbsolutePath() + File.separator;
        } else {
            vinFile = Environment.getExternalStorageDirectory();
            prefix = vinFile.getAbsolutePath() + File.separator;
        }
        if (success) {
            return prefix  + "success_" + System.currentTimeMillis() + "_" + vinCode + ".png";
        }
        return prefix + "fail_" + "_" + System.currentTimeMillis() + ".png";
    }

}
