package com.getech.android.httphelper.httpinterface;

import java.io.File;
import java.io.InputStream;

/**
 * <pre>
 *     author : xiaoqing
 *     time   : 2019/02/28
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public abstract class FileConvert {
    /**
     * 目标文件存储的文件夹路径
     */
    protected String folder;
    /**
     * 目标文件存储的文件名
     */
    protected String fileName;
    protected FileDownloadProgressListener fileDownloadProgressListener;

    /**
     * 将流转为文件
     *
     * @param inputStream   输入流
     * @param contentLength 流长度
     * @return
     */
    public abstract File convert(InputStream inputStream, long contentLength) throws Exception;
}
