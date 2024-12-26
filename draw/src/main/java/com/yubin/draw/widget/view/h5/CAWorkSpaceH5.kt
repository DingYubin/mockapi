package com.yubin.draw.widget.view.h5

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.webkit.GeolocationPermissions
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.JsonParser
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.R
import com.yubin.engine.webview.CassWebView
import com.yubin.engine.webview.ICassWebViewInterface
import org.json.JSONObject

/**
 * description 开思助手H5
 *
 */
class CAWorkSpaceH5 : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    companion object {
        /**
         * 查看文件文档
         */
        const val APP_VIEW_DOCUMENT = "viewDocument"

        /**
         * 查看图片
         */
        const val APP_VIEW_PHOTO = "viewPhoto"

        /**
         * 查看视频
         */
        const val APP_VIEW_VIDEO = "viewVideo"

        /**
         * 跳转页面
         */
        const val APP_FORWARD_PAGE = "forwardPage"

        /**
         * 设置H5标题
         */
        const val APP_SET_H5_TITLE = "setH5Title"

        /**
         * 选择联系人
         */
        const val APP_PICK_CONTACTS = "pickContacts"

        /**
         * 关闭页面
         */
        const val APP_CLOSE_PAGE = "closePage"

        /**
         * 原生事件--H5给原生发送事件
         */
        const val APP_POST_EVENT = "postEvent"

        /**
         * 原生返回事件--回调给H5
         */
        const val H5_ON_BACK_PRESSED = "onBackPressed"

        /**
         * 原生返回事件--Tab切换
         */
        const val H5_ON_APP_TAB_CHANGE = "onAppTabChange"

    }

    private var h5Process: ProgressBar? = null
    private var baseUrl: String? = null
    private var activity: ComponentActivity? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    var cassWebView: CassWebView? = null
    var pickContacts: (callId: String) -> Unit = { _: String -> }
    var openDocument: (result: (Uri) -> Unit) -> Unit = { }
    var setH5Title: (title: String) -> Unit = {}

    init {

        View.inflate(context, R.layout.view_workspace_h5, this)
        activity = context as? ComponentActivity

        h5Process = findViewById(R.id.h5LoadProgress)
        cassWebView = findViewById(R.id.cassWebView)

        cassWebView?.cassWebViewInterface = object : ICassWebViewInterface {
            override fun callAppFunction(
                methodName: String,
                callId: String,
                paramsJson: String
            ): String? {
                LogUtil.i("callAppFunction: $methodName $paramsJson")
                when (methodName) {
                    APP_VIEW_DOCUMENT -> {
//                        CECActionHelper.forward2NativePage(
//                            CARoutePath.Foundation.PATH_FILE_DOWNLOAD,
//                            paramsJson
//                        )
                    }

                    APP_VIEW_PHOTO -> {
//                        val json = JsonParser.parseString(paramsJson).asJsonObject
//                        val title = json.get("title").asString
//                        val imageIndex = json.get("imageIndex").asInt
//                        val list = ArrayList<CassNetImagePickerBean>()
//                        val urls = json.get("urls").asJsonArray
//                        urls.forEach {
//                            list.add(CassNetImagePickerBean().apply {
//                                this.url = it.asString
//                                type = CassNetVideoAndNetImgStrategy.MEDIA_TYPE_IMAGE
//                            })
//                        }
//                        val strategy =
//                            CassNetVideoAndNetImgStrategy(title, imageIndex, false, list, null)
//                        CassMediaUtil.startShowMedia(context as Activity, strategy, null)
                    }

                    APP_VIEW_VIDEO -> {
//                        val json = JsonParser.parseString(paramsJson).asJsonObject
//                        val url = json.get("url").asString
//                        val isMute = json.get("isMute").asBoolean
//                        CassMediaUtil.startVideoPlay(
//                            CassVideoStrategy(
//                                url = url,
//                                isMute = isMute,
//                                isNeedHide = true
//                            ), context as Activity, null
//                        )
                    }

                    APP_FORWARD_PAGE -> {
//                        val json = JsonParser.parseString(paramsJson).asJsonObject
//                        val routerUri = json.get("routerUri").asString
//                        CECActionService.getInstance().performWithUri(routerUri)
                    }

                    APP_SET_H5_TITLE -> {
                        val json = JsonParser.parseString(paramsJson).asJsonObject
                        val title = json.get("title").asString
                        setH5Title.invoke(title)
                    }

                    APP_PICK_CONTACTS -> {
                        pickContacts.invoke(callId)
                    }

                    APP_CLOSE_PAGE -> {
                        (context as? Activity)?.finish()
                    }

                    APP_POST_EVENT -> {
//                        val json = JsonParser.parseString(paramsJson).asJsonObject
//                        val eventId = json.get("eventId").asString
//                        val eventParams = json.get("eventParams").asString
//                        val map: Map<*, *> = Gson().fromJson(eventParams, Map::class.java)
//                        CassEventBusHelper.post(CassEvent(eventId, map))
                    }
                }
                return null
            }
        }

        cassWebView?.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                h5Process?.setProgress(newProgress, true)
                LogUtil.i("onProgressChanged: $newProgress")
                if (newProgress == 100) {
                    h5Process?.visibility = View.INVISIBLE
                } else {
                    h5Process?.visibility = View.VISIBLE
                }
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                LogUtil.i("onGeolocationPermissionsShowPrompt")
//                CECPermissionHelper.requestForLocation(
//                    context as Activity,
//                    object : CECPermissionHelper {
//                        override fun onPermissionDeclined(permission: String?) {
//                            callback?.invoke(origin, false, true)
//                        }
//
//                        override fun onPermissionDenied(permission: String?) {
//                            callback?.invoke(origin, false, true)
//                        }
//
//                        override fun onPermissionGranted() {
//                            callback?.invoke(origin, true, true)
//                        }
//
//                    },
//                    "开启位置权限，完成定位打卡。"
//                )
            }

            override fun onGeolocationPermissionsHidePrompt() {
                super.onGeolocationPermissionsHidePrompt()
                LogUtil.i("onGeolocationPermissionsHidePrompt")
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@CAWorkSpaceH5.filePathCallback = filePathCallback

                if (fileChooserParams?.acceptTypes?.contains("image/*") == true) {
                    //跳转相册
//                    CassMediaUtil.startAlbum(
//                        CassAlbumStrategy(isShowVideo = true, maxCount = 9),
//                        activity!!,
//                        object : ICassMediaCallBack {
//                            override fun result(medias: ArrayList<CassMediaInfo>) {
//                                val arrayUri = arrayListOf<Uri>()
//                                medias.forEach { media ->
//                                    arrayUri.add(Uri.parse(media.uri))
//                                }
//                                this@CAWorkSpaceH5.filePathCallback = null
//                                filePathCallback?.onReceiveValue(arrayUri.toTypedArray())
//                            }
//                        })
                } else {
                    this@CAWorkSpaceH5.openDocument.invoke {
                        this@CAWorkSpaceH5.filePathCallback = null
                        filePathCallback?.onReceiveValue(arrayOf(it))
                    }
                }
                return true
            }
        }

        cassWebView?.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                LogUtil.i("shouldOverrideUrlLoading")
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                h5Process?.setProgress(100, true)
                LogUtil.i("onPageFinished")
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                LogUtil.i("onPageStarted")
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
                LogUtil.i("doUpdateVisitedHistory:${url}")
            }
        }

    }

    fun onBackPressed() {
        cassWebView?.callH5Function(H5_ON_BACK_PRESSED, "", null)
    }

    fun onAppTabChange(tabName: String) {
        cassWebView?.callH5Function(H5_ON_APP_TAB_CHANGE, JSONObject().apply {
            put("tabName", tabName)
        }.toString(), null)
    }

    fun destroyWebView() {
        cassWebView?.stopLoading()
        cassWebView?.destroy()
    }

    fun loadUrlAndSyncCookie(url: String) {
        this.baseUrl = url
        cassWebView?.syncCookie(
            url,
            arrayListOf("phone=" + "CAUserInfoHelper.getUserInfo()?.cellphone")
        )
        cassWebView?.setUserAgent("CAUserInfoHelper.userAgent")
        cassWebView?.loadUrl(url)
    }

    fun clearWebView() {
        cassWebView?.clearCache(true) // 清除网页内容缓存
        cassWebView?.evaluateJavascript("localStorage.clear();", null) // 清除 localStorage
        cassWebView?.evaluateJavascript("sessionStorage.clear();", null)
    }

    fun onHostResume() {
        try {
            this@CAWorkSpaceH5.filePathCallback?.onReceiveValue(arrayOf(Uri.parse("")))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}