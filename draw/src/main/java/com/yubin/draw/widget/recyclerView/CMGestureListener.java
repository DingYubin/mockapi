package com.yubin.draw.widget.recyclerView;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * description: 对recyclerView的手势监听处理
 * date: created on 2019/2/22
 * <p>
 * author:   侯军(A01082)
 */
public abstract class CMGestureListener extends GestureDetector.SimpleOnGestureListener {

    private View mTargetView;
    private RecyclerView mRecyclerView;

    protected CMGestureListener(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        if (mRecyclerView == null) {
            return super.onDown(event);
        }
        mTargetView = mRecyclerView.findChildViewUnder(event.getX(), event.getY());
        return mTargetView != null;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        if (mTargetView == null) {
            return;
        }
        mTargetView.setPressed(true);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        if (mTargetView == null || mRecyclerView == null || mRecyclerView.getAdapter() == null) {
            return super.onSingleTapUp(event);
        }
        mTargetView.setPressed(false);
        int position = mRecyclerView.getChildAdapterPosition(mTargetView);
        if (position == -1) {
            return super.onSingleTapUp(event);
        }
        long id = mRecyclerView.getAdapter().getItemId(position);
        boolean handled = onItemClick(mRecyclerView, mTargetView, position, id);
        mTargetView = null;
        return handled;
    }

    @Override
    public boolean onScroll(MotionEvent event, MotionEvent event2, float v, float v2) {
        if (mTargetView == null) {
            return super.onScroll(event, event2, v, v2);
        }
        mTargetView.setPressed(false);
        mTargetView = null;
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        if (mTargetView == null || mRecyclerView == null || mRecyclerView.getAdapter() == null) {
            return;
        }
        int position = mRecyclerView.getChildAdapterPosition(mTargetView);
        if (position == -1) {
            return;
        }
        long id = mRecyclerView.getAdapter().getItemId(position);
        boolean handled = onItemLongClick(mRecyclerView, mTargetView, position, id);
        if (handled) {
            mTargetView.setPressed(false);
            mTargetView = null;
        }
    }

    /**
     * onItemClick callBack
     *
     * @param recyclerView {@link RecyclerView}
     * @param view         clicked view {@link View}
     * @param position     position of click view in adapter
     * @param id           itemId of click view
     * @return true 事件已消费 false 事件未消费
     */
    public abstract boolean onItemClick(RecyclerView recyclerView, View view, int position, long id);

    /**
     * onItemLongClick callBack
     *
     * @param recyclerView {@link RecyclerView}
     * @param view         clicked view {@link View}
     * @param position     position of click view in adapter
     * @param id           itemId of click view
     * @return true 事件已消费 false 事件未消费
     */
    public abstract boolean onItemLongClick(RecyclerView recyclerView, View view, int position, long id);
}
