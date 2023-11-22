package com.getech.android.httphelper.httpinterface.rx;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/01/17
 *     desc   : 上传下载结果信息
 *     version: 1.0
 * </pre>
 */
public class RxUploadDownloadResult<T> {
    /**
     * 上传下载中
     */
    public static final int UPLOAD_DOWNLOAD_STATUS_PROCESSING = 0;
    /**
     * 已完成
     */
    public static final int UPLOAD_DOWNLOAD_STATUS_FINISHED = 1;
    /**
     * 被取消
     */
    public static final int UPLOAD_DOWNLOAD_STATUS_CANCELED = 2;
    /**
     * 上传下载状态，0：上传下载中，1：完成，2：被取消
     */
    public int status;
    /**
     * 下载进度，0-100
     */
    public int progress;
    /**
     * 已经下载的大小
     */
    public long process;
    /**
     * 文件大小
     */
    public long total;
    /**
     * 成功后返回的数据
     */
    public T response;
    /**
     * 网络请求tag
     */
    public String tag;

    public RxUploadDownloadResult(int progress, long process, long total, T response, String tag, int status) {
        this.progress = progress;
        this.response = response;
        this.process = process;
        this.total = total;
        this.tag = tag;
        this.status = status;
    }
}
