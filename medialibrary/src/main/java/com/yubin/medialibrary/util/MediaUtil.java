package com.yubin.medialibrary.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * description 多媒体文件工具
 *
 * @author yubin
 */
public class MediaUtil {

    private MediaUtil() {

    }

    // 通知图库更新
    public static void mediaScan(Context context, File file) {
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null,
                (path, uri) -> {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(uri);
                    context.sendBroadcast(mediaScanIntent);
                });
    }

    public static Bitmap decodeVideoThumb(String videoFilePath) {
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            File file = new File(videoFilePath);
            mmr.setDataSource(file.getAbsolutePath());
            Bitmap bitmap;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
                bitmap = mmr.getScaledFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, 640, 480);
            } else {
                bitmap = mmr.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                if (bitmap != null) {
                    int w = bitmap.getWidth();
                    int h = bitmap.getHeight();
                    if (w > 640 || h > 480) {
                        Matrix matrix = new Matrix();
                        float scale = Math.min(640f / w, 480f / h);
                        matrix.postScale(scale, scale);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, false);
                    }
                }
            }
            return bitmap;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (null != mmr) {
                mmr.release();
            }
        }
        return null;
    }

    public static String encodeBitmap2String(Bitmap thumbBitmap) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            if (null != thumbBitmap) {
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos);
                thumbBitmap.recycle();
                return Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
