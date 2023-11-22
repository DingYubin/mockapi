package com.getech.android.httphelper.httpinterface;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/02/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface FileDownloadProgressListener {
    void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed);
}
