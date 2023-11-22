package com.getech.android.httphelper.impl;

import com.getech.android.httphelper.httpinterface.FileConvert;
import com.getech.android.httphelper.httpinterface.FileDownloadProgressListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.utils.OkLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/02/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class OkGoFileConvert extends FileConvert {
    public OkGoFileConvert(String folder, String fileName, FileDownloadProgressListener fileDownloadProgressListener) {
        this.folder = folder;
        this.fileName = fileName;
        this.fileDownloadProgressListener = fileDownloadProgressListener;
    }

    @Override
    public File convert(InputStream inputStream, long contentLength) throws Exception {
        File dir = new File(folder);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        if (file.exists()) {
            file.delete();
        }
        //最后一次刷新的时间
        long lastRefreshUiTime = 0;
        //最后一次写入字节数据
        long lastWriteBytes = 0;

        InputStream is = inputStream;
        byte[] buf = new byte[2048];
        FileOutputStream fos = null;
        try {
            final long total = contentLength;
            long sum = 0;
            int len;
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);

                //下载进度回调
                if (fileDownloadProgressListener != null) {
                    final long finalSum = sum;
                    long curTime = System.currentTimeMillis();
                    //每1秒刷新一次数据
                    if ((curTime - lastRefreshUiTime >= OkGo.REFRESH_TIME * 5) || finalSum == total) {
                        //计算下载速度
                        long diffTime = (curTime - lastRefreshUiTime) / 1000;
                        if (diffTime == 0) {
                            diffTime += 1;
                        }
                        long diffBytes = finalSum - lastWriteBytes;
                        final long networkSpeed = diffBytes / diffTime;
                        OkGo.getInstance().getDelivery().post(new Runnable() {
                            @Override
                            public void run() {
                                //进度回调的方法
                                fileDownloadProgressListener.downloadProgress(finalSum, total, finalSum * 1.0f / total, networkSpeed);
                            }
                        });

                        lastRefreshUiTime = System.currentTimeMillis();
                        lastWriteBytes = finalSum;
                    }
                }
            }
            fos.flush();
            return file;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                OkLogger.e(e.getMessage());
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                OkLogger.e(e.getMessage());
            }
        }
    }
}
