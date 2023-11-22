package com.getech.android.httphelper.httpinterface;

/**
 * <pre>
 *     author : xiaoqing
 *     e-mail : qing.xiao@getech.cn
 *     time   : 2019/01/07
 *     desc   : http请求接口
 *     version: 1.0
 * </pre>
 */
public abstract class AbstractHttpRequestListener<T> {
    private ResultConverter<T> converter;
    private HttpErrorProcessor httpErrorProcessor;

    /**
     * Instantiates a new Http request listener.
     */
    public AbstractHttpRequestListener() {
    }

    /**
     * Instantiates a new Http request listener.
     *
     * @param converter the converter
     */
    public AbstractHttpRequestListener(ResultConverter<T> converter) {
        this.converter = converter;
    }

    /**
     * Instantiates a new Http request listener.
     *
     * @param httpErrorProcessor the http error processor
     */
    public AbstractHttpRequestListener(HttpErrorProcessor httpErrorProcessor) {
        this.httpErrorProcessor = httpErrorProcessor;
    }

    /**
     * Instantiates a new Http request listener.
     *
     * @param converter          the converter
     * @param httpErrorProcessor the http error processor
     */
    public AbstractHttpRequestListener(ResultConverter<T> converter, HttpErrorProcessor httpErrorProcessor) {
        this.converter = converter;
        this.httpErrorProcessor = httpErrorProcessor;
    }

    /**
     * 收到结果
     *
     * @param tag the tag
     * @param t   the t
     */
    public abstract void onReceiveResult(String tag, T t);

    /**
     * 业务自己处理response
     *
     * @param tag         the tag
     * @param responseStr the response str
     */
    public abstract void customProcessResult(String tag, String responseStr);

    /**
     * 处理 error信息
     *
     * @param tag the tag
     * @param e   the e
     */
    public abstract void processError(String tag, Exception e);

    /**
     * 成功
     *
     * @param tag         the tag
     * @param responseStr 返回数据，如果是下载，则为文件路径
     * @throws Throwable the throwable
     */
    public final void onSuccess(String tag, String responseStr) throws Throwable {
        if (converter != null) {
            T t = converter.convertResponse(responseStr);
            onReceiveResult(tag, t);
        } else {
            customProcessResult(tag, responseStr);
        }
    }

    /**
     * 成功
     *
     * @param tag the tag
     */
    public void onFinish(String tag) {

    }

    /**
     * http请求开始
     *
     * @param tag the tag
     */
    public void onStart(String tag) {

    }

    /**
     * 出错
     *
     * @param tag       the tag
     * @param errorInfo the error info
     */
    public final void onError(String tag, HttpErrorInfo errorInfo) {
        if (httpErrorProcessor != null) {
            httpErrorProcessor.processHttpError(tag, errorInfo);
        } else {
            processError(tag, errorInfo);
        }
    }

    /**
     * 进度
     *
     * @param tag             the tag
     * @param progress        百分比 0-100
     * @param numberProcessed 已处理的数量
     * @param total           总数量
     */
    public void onProgress(String tag, int progress, long numberProcessed, long total) {

    }

    public ResultConverter<T> getConverter() {
        return converter;
    }
}
