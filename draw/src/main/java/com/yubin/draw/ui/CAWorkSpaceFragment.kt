package com.yubin.draw.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.yubin.baselibrary.extension.onViewClick
import com.yubin.baselibrary.ui.mvvm.CassBaseFragment
import com.yubin.baselibrary.util.LogUtil
import com.yubin.draw.databinding.FragmentWorkspaceBinding
import com.yubin.draw.widget.view.h5.CAWorkSpaceH5

/**
 * description 开思助手工作台
 *
 * @author laiwei
 * @date 2024/10/21 14:03
 */
class CAWorkSpaceFragment : CassBaseFragment<FragmentWorkspaceBinding>() {

    companion object {
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
        this.initViewAndListener()
        this.initWebView()
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

        activity?.let {
            it.onBackPressedDispatcher.addCallback(it, object :
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.workSpaceBack.performClick()
                }
            })
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
        binding.minimizeButton.onViewClick {
            LogUtil.i("CAWorkSpaceH5 浮窗最小化")
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