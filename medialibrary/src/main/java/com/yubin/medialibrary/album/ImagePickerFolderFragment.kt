package com.yubin.medialibrary.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.yubin.baselibrary.ui.basemvvm.BaseFragment
import com.yubin.medialibrary.R
import com.yubin.medialibrary.album.adapter.ImagePickerFolderAdapter
import com.yubin.medialibrary.album.been.ImagePickerBean
import com.yubin.medialibrary.album.decoration.ImagePickerFolderItemDecoration
import com.yubin.medialibrary.camera.MediaManager
import com.yubin.medialibrary.databinding.FragmentImagePickerFolderBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * description 照片文件夹列表
 *
 * @author laiwei
 * @date create at 2019-07-12 14:12
 */
class ImagePickerFolderFragment : BaseFragment<FragmentImagePickerFolderBinding>() {
    private var folders: List<ImagePickerBean>? = null
    private var mViewModel: ImagePickerViewModel? = null
    private var currentPosition = 0
    private val enterAnimation: Animation? = null
    private val exitAnimation: Animation? = null
    private var folderAdapter: ImagePickerFolderAdapter? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentImagePickerFolderBinding {
        return FragmentImagePickerFolderBinding.inflate(inflater, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.onLazyLoadData()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments == null) {
            return
        }
        folders = arguments?.getSerializable("folders") as List<ImagePickerBean>?
        currentPosition = arguments?.getInt("position")!!
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentImagePickerFolderRecycler.addItemDecoration(
            ImagePickerFolderItemDecoration(context)
        )
        binding.fragmentImagePickerFolderRecycler.layoutManager = LinearLayoutManager(context)
        folderAdapter =
            ImagePickerFolderAdapter(IImageFolderSelectListener { folder, position ->
                if (null != mViewModel) {
                    if (currentPosition != position) {
                        mViewModel?.folderMutableLiveData?.value = folder
                    } else {
                        mViewModel?.folderMutableLiveData?.value = null
                    }
                }
                folders?.get(currentPosition)?.isFolderSelect = false
                folders?.get(position)?.isFolderSelect = true
                currentPosition = position
                folderAdapter?.notifyDataSetChanged()
                GlobalScope.launch {
                    MediaManager.instance.saveValue(
                        ImagePickerActivity.IMAGE_LAST_FOLDER,
                        folder
                    )
                }
            })
        binding.fragmentImagePickerFolderRecycler.adapter = folderAdapter
        folderAdapter?.submitList(folders)
        binding.fragmentImagePickerFolderBg.setOnClickListener(View.OnClickListener {
            if (activity != null && activity?.supportFragmentManager != null) {
                val fm = activity?.supportFragmentManager
                val bt = fm?.beginTransaction()
                bt?.hide(this)
                bt?.commit()
            }
        })

        val enterAnimation =
            AnimationUtils.loadAnimation(context, R.anim.anim_enter_from_bottom);
        val exitAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_exit_to_bottom)
        enterAnimation.setAnimationListener(object : AnimationListener {

            override fun onAnimationRepeat(animation: Animation?) {
                //onAnimationRepeat
            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.fragmentImagePickerFolderBg.visibility = View.VISIBLE
            }

            override fun onAnimationStart(animation: Animation?) {
                binding.fragmentImagePickerFolderBg.visibility = View.GONE
            }
        })

        exitAnimation.setAnimationListener(object : AnimationListener {

            override fun onAnimationRepeat(animation: Animation?) {
                //onAnimationRepeat
            }

            override fun onAnimationEnd(animation: Animation?) {
            }

            override fun onAnimationStart(animation: Animation?) {
                binding.fragmentImagePickerFolderBg.visibility = View.GONE
            }
        })
    }


    private fun onLazyLoadData() {
        if (null != activity) {
            mViewModel = ViewModelProviders.of(requireActivity()).get(
                ImagePickerViewModel::class.java
            )
        }
    }

    override fun onCreateAnimation(
        transit: Int,
        enter: Boolean,
        nextAnim: Int
    ): Animation? {
        return if (enter) {
            enterAnimation
        } else {
            exitAnimation
        }
    }


    companion object {
        fun newInstance(
            folders: ArrayList<ImagePickerBean>,
            position: Int
        ): ImagePickerFolderFragment {
            val f = ImagePickerFolderFragment()
            val bundle = Bundle()
            bundle.putSerializable("folders", folders)
            bundle.putInt("position", position)
            f.arguments = bundle
            return f
        }
    }
}