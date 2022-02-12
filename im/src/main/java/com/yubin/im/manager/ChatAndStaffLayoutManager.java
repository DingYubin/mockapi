package com.yubin.im.manager;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * @author xiaoqqq
 * @package com.getech.teamwork.ui.main.contact
 * @date 2019-05-29
 * @describe 首页消息列表的layoutmanager
 */
public class ChatAndStaffLayoutManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public ChatAndStaffLayoutManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }

//    @Override
//    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
//        LinearSmoothScroller smoothScroller =
//                new LinearSmoothScroller(recyclerView.getContext()) {
//                    // 返回：滑过1px时经历的时间(ms)。
//                    @Override
//                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
//                        return 10f / displayMetrics.densityDpi;
//                    }
//                };
//
//        smoothScroller.setTargetPosition(position);
//        startSmoothScroll(smoothScroller);
//    }
}
