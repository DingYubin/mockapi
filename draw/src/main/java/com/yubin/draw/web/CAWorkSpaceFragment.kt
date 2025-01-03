package com.yubin.draw.web

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.ui.mvvm.CassBaseFragment
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.R
import com.yubin.draw.databinding.FragmentWorkspaceBinding
import com.yubin.draw.manager.FloatingWebWindowManager
import com.yubin.draw.web.adapt.WebViewScreenshotAdapter
import com.yubin.draw.widget.view.h5.CAWorkSpaceH5
import com.yubin.draw.widget.window.FloatingWebWindow

/**
 * description 开思助手工作台
 *
 * @author laiwei
 * @date 2024/10/21 14:03
 */
class CAWorkSpaceFragment : CassBaseFragment<FragmentWorkspaceBinding>() {

    companion object {

        private const val KEY_WEB_VIEW_STATE = "web_view_state"
        private const val KEY_LAST_URL = "last_url"

        fun newInstance(
            baseUrl: String,
            tabName: String,
            inHome: Boolean = true
        ): CAWorkSpaceFragment {
            val fragment = CAWorkSpaceFragment()
            val args = Bundle()
            args.putString("baseUrl", baseUrl)
            args.putBoolean("inHome", inHome)
            args.putString("tabName", tabName)
            fragment.arguments = args
            return fragment
        }
    }

    private var tabName: String = ""
    private var baseUrl: String = ""
    private var inHome: Boolean = true
    private var pickContactsStartForResult: ActivityResultLauncher<Intent>? = null
    private var openDocumentTreeLauncher: ActivityResultLauncher<Array<String>?>? = null
    private var openDocumentResult: ((Uri) -> Unit)? = null
    private lateinit var relativeLayout: RelativeLayout
    private var recyclerView: RecyclerView? = null
    private val screenshotAdapter = WebViewScreenshotAdapter()
    private val floatingWindow by lazy { FloatingWebWindow.getInstance() }

    private var callId: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pickContactsStartForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val value = result.data?.getStringExtra("contact") ?: ""
                binding.workSpaceWeb.cassWebView?.evaluateAppCallback(
                    CAWorkSpaceH5.APP_PICK_CONTACTS,
                    callId ?: "",
                    value
                )
            }
        }
        openDocumentTreeLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                if (uri != null) {
                    // 获取所选文件的 Uri
                    openDocumentResult?.invoke(uri)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseUrl = arguments?.getString("baseUrl") ?: ""
        inHome = arguments?.getBoolean("inHome") ?: true
        tabName = arguments?.getString("tabName") ?: ""
        relativeLayout = RelativeLayout(requireContext())
        this.initView()
        this.initViewAndListener()
        this.initWebView()
    }

    private fun initView() {
        // 创建RecyclerView用于悬浮窗
        recyclerView = context?.let {
            RecyclerView(it).apply {
                layoutManager = LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false)
                adapter = screenshotAdapter

                // 添加item间距
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.left = 8
                        outRect.right = 8
                    }
                })

            }
        }
    }

    private fun initViewAndListener() {
//        binding.refreshLayout.setOnRefreshListener {
//            binding.workSpaceWeb.reloadWebView()
//            binding.refreshLayout.isRefreshing = false
//        }
        if (inHome) {
            binding.titleBar.visibility = View.GONE
            return
        } else {
            binding.root.setPadding(0, 0, 0, 0)
        }

        binding.workSpaceBack.onViewClick {
            val webView = binding.workSpaceWeb.cassWebView
            if (webView == null) {
                activity?.finish()
                return@onViewClick
            }
            if (!webView.canGoBack()) {
                activity?.finish()
                return@onViewClick
            }
            val copyBackForwardList = webView.copyBackForwardList()
            if (copyBackForwardList.size == 2) {
                val rootItem = copyBackForwardList.getItemAtIndex(0)
                val url = rootItem.url
                val originalUrl = rootItem.originalUrl
                if (url != originalUrl && originalUrl == baseUrl) {
                    activity?.finish()
                    return@onViewClick
                }
            }

            webView.goBack()
        }
        binding.workSpaceClose.onViewClick {
            activity?.finish()
        }

        binding.minimizeButton.onViewClick {
            val webView = binding.workSpaceWeb.cassWebView
            if (webView == null) {
                activity?.finish()
                return@onViewClick
            }

//            val view = initWindowView(captureWebView(webView))
//
//            FloatingWebWindowManager.addView(view)
//            relativeLayout.removeAllViews()

//            updateWindows()
            minimizeToFloating()
            screenshotAdapter.addScreenshot(webView)
        }

        activity?.let {
            it.onBackPressedDispatcher.addCallback(it, object :
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.workSpaceBack.performClick()
                }
            })
        }
    }

    private fun minimizeToFloating() {

        if (screenshotAdapter.screenshots.isEmpty()) {
            context?.let { context ->
                if (!requestOverlayPermission(context)) {
//                if (FloatingWebWindowManager.isEmpty()) {
//                    mFloatingWindow?.dismiss()
//                } else {

                    // 显示悬浮窗
                    recyclerView?.let { floatingWindow.showFloatingWindow(context, it) }
                    // 隐藏Fragment
//                    hideFragment()
//                }

                } else {
                    Toast.makeText(context, "请开启悬浮窗权限", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun updateWindows() {
        FloatingWebWindowManager.webViews.forEach {
            addViewWithOffset(it)
        }

        activity?.let {
            if (!requestOverlayPermission(it)) {
                if (FloatingWebWindowManager.isEmpty()) {
                    floatingWindow.dismiss()
                } else {
//                    mFloatingWindow?.showFloatingWindow(it, relativeLayout)
                }

            } else {
                Toast.makeText(it, "请开启悬浮窗权限", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addViewWithOffset(view: View) {

        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        layoutParams.addRule(RelativeLayout.ABOVE, View.generateViewId())
        // 检查新视图是否已经有父视图，如果有，则先将其从原父视图中移除
        if (view.parent != null && view.parent is ViewGroup) {
            (view.parent as ViewGroup).removeView(view)
        }
        relativeLayout.addView(view, layoutParams)
    }

    private fun captureWebView(webView: WebView): Bitmap {
        val width = webView.width
        val height = webView.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        webView.draw(canvas)
        return bitmap
    }

    /**
     * 浮窗样式
     */
    private fun initWindowView(web: Bitmap): View {
        val view = View.inflate(activity, R.layout.view_video_view_floating_web, null)
        // 设置视频封面
        val mThumb = view.findViewById<View>(R.id.thumb_floating_view) as ImageView
        mThumb.setImageBitmap(web)
//        Glide.with(this).load(R.drawable.thumb).into(mThumb)

        // 悬浮窗关闭
        view.findViewById<View>(R.id.close_floating_view)
            .onViewClick{
                    if (FloatingWebWindowManager.isEmpty()) {
                        return@onViewClick
                    }

                    FloatingWebWindowManager.removeView()
                    if (view.parent != null && view.parent is ViewGroup) {
                        (view.parent as ViewGroup).removeView(view)
                    }
                    updateWindows()
            }
        return view
    }

    /**
     * 判断是否开启悬浮窗口权限，否则，跳转开启页
     */
    private fun requestOverlayPermission(context: Context): Boolean {
        return if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(
                    "package:${context.packageName}"
                )
            )
            resultLauncher.launch(intent)
            true
        } else {
            false
        }
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            if (Settings.canDrawOverlays(activity)) {
//                showFloatingWindow()
            } else {
                Toast.makeText(activity, "获取悬浮窗权限失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initWebView() {
        binding.workSpaceWeb.pickContacts = { callId ->
            this.callId = callId
            pickContactsStartForResult?.launch(
                Intent(
                    this.activity,
                    Class.forName("com.yubin.assistant.account.contacts.CAContactsActivity")
                ).apply {
                    putExtra("KEY_FROM_PAGE", "PICK_CONTACTS")
                }
            )
        }
        binding.workSpaceWeb.openDocument = {
            this.openDocumentResult = it
            openDocumentTreeLauncher?.launch(arrayOf("*/*"))
        }
        binding.workSpaceWeb.setH5Title = {
            if (!inHome) {
                binding.workSpaceTitle.text = it
            }
        }
        binding.workSpaceWeb.loadUrlAndSyncCookie(baseUrl)
    }

    fun onAppTabChange() {
        binding.workSpaceWeb.onAppTabChange(tabName)
        LogUtil.i("onAppTabChange=$tabName")
    }

    override fun onResume() {
        super.onResume()
        binding.workSpaceWeb.onHostResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.workSpaceWeb.destroyWebView()
    }

}