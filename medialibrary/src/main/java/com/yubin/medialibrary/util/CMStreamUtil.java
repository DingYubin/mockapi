package com.yubin.medialibrary.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * description:
 *
 * @author yubin
 * @date 2021/8/20
 */
public class CMStreamUtil {

    /**
     * 根据路径获得图片并压缩，返回bitmap(票小秘压缩方案)
     *
     * @param filePath filePath
     * @return Bitmap
     */
    public static Bitmap getSmallBitmap(String filePath, int srcWidth, int srcHeight) {
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            // Calculate inSampleSize
            options.inSampleSize = computeSize(srcWidth, srcHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
            return bitmap;
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
        return null;
    }


    /**
     * 这里的算法是以短边压缩到1000~2000之间为目标，通过计算到1000的比值，然后需要将采样率控制为2的倍数
     * 所以需要使用方法{@link #calInSampleSize(int)}进行计算
     * (票小秘压缩方案)
     *
     * @return 采样率
     */
    public static int computeSize(int srcWidth, int srcHeight) {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int shortSide = Math.min(srcWidth, srcHeight);

        int rate = (int) Math.floor(shortSide / 1000.0);

        return calInSampleSize(rate);
    }

    /**
     * 通过移位操作计算采样率，是某个整数对应的二进制数保留最高位为1，其他位置为0的结果
     * (票小秘压缩方案)
     *
     * @param rate 比例
     * @return 采样率
     */
    private static int calInSampleSize(int rate) {
        int i = 0;
        while ((rate >> (++i)) != 0) ;
        return 1 << --i;
    }

}
