package com.yubin.draw.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yubin.draw.R
import com.yubin.draw.widget.viewGroup.im.ImTabGroupMemberView

class TestFragment1 : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_test1, container, false)
    }
    

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.im_tab).setOnClickListener {
            val context = requireContext()
            val atPopWindow = ImTabGroupMemberView(context)
            val activity = requireActivity() as TabActivity

            atPopWindow.showImAtGroupMemberWindow(activity.window.decorView)
            atPopWindow.setData()
        }
    }
}