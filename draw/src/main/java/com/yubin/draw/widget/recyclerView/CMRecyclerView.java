package com.yubin.draw.widget.recyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.yubin.draw.widget.recyclerView.protocol.ICMOnItemClickListener;
import com.yubin.draw.widget.recyclerView.protocol.ICMOnItemLongClickListener;


/**
 * description: 自定义recyclerView(实现itemClick和itemLongClick)
 * <p>
 * date: created on 2019/2/22
 * <p>
 */
@SuppressWarnings("rawtypes")
public class CMRecyclerView extends RecyclerView {

    private View mEmptyView;
    private GestureDetector mGestureDetector;
    private ICMOnItemClickListener mOnItemClickListener;
    private ICMOnItemLongClickListener mOnItemLongClickListener;

    public CMRecyclerView(@NonNull Context context) {
        super(context);
    }

    public CMRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CMRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if (null != mGestureDetector) {
            mGestureDetector.onTouchEvent(event);
        }
        return result;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
    }

    @Override
    public void swapAdapter(@Nullable Adapter adapter, boolean removeAndRecycleExistingViews) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(mObserver);
        }
        super.swapAdapter(adapter, removeAndRecycleExistingViews);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
    }

    /**
     * 设置没有内容时，提示用户的空布局
     */
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    /**
     * 设置没有内容时，提示用户的空布局
     */
    public void setEmptyView(View emptyView, boolean isCheck) {
        this.mEmptyView = emptyView;
        if (isCheck) {
            this.checkIfEmptyWhenChanged();
        }
    }

    /**
     * 加载数据完成
     */
    public void loadDataComplete(boolean isShowEmpty) {
        if (mEmptyView == null) {
            return;
        }
        mEmptyView.setVisibility(isShowEmpty ? View.VISIBLE : View.GONE);
        this.setVisibility(isShowEmpty ? View.GONE : View.VISIBLE);
        if (isShowEmpty) {
            try {
                this.scrollToPosition(0);
            } catch (Exception ignored) {
                //do nothing
            }
        }
    }

    /**
     * 设置itemClick事件监听器
     *
     * @param onItemClickListener itemClick事件监听器 {@link ICMOnItemLongClickListener}
     */
    public void setOnItemClickListener(ICMOnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        if (onItemClickListener != null) {
            this.createGestureDetectorIfNecessary();
        }
    }

    public ICMOnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    /**
     * 设置itemLongClick事件监听器
     *
     * @param onItemLongClickListener itemLongClick事件监听器 {@link ICMOnItemLongClickListener}
     */
    public void setOnItemLongClickListener(ICMOnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
        if (!this.isLongClickable()) {
            this.setLongClickable(true);
        }
        if (onItemLongClickListener != null) {
            this.createGestureDetectorIfNecessary();
        }
    }

    /**
     * 是否已经滑动在最底部
     *
     * @return true，则滑动最底部，false，则没有
     */
    public boolean isScroll2Bottom() {
        return this.computeVerticalScrollExtent() + this.computeVerticalScrollOffset() >= this.computeVerticalScrollRange();
    }

    /**
     * 是否已经滑动到最右端
     *
     * @return true，则滑动最底部，false，则没有
     */
    public boolean isScroll2Right() {
        return this.computeHorizontalScrollExtent() + this.computeHorizontalScrollOffset() >= this.computeHorizontalScrollRange();
    }

    /**
     * 创建gestureDetector
     */
    private void createGestureDetectorIfNecessary() {
        if (mGestureDetector != null) {
            return;
        }
        mGestureDetector = new GestureDetector(getContext(), new CMGestureListener(this) {
            @Override
            public boolean onItemClick(RecyclerView recyclerView, View view, int position, long id) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(recyclerView, view, position, id);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onItemLongClick(RecyclerView recyclerView, View view, int position, long id) {
                return mOnItemLongClickListener != null && mOnItemLongClickListener.onItemLongClick(recyclerView, view, position, id);
            }
        });
    }

    /**
     * 适配器数据换插着
     */
    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmptyWhenChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmptyWhenChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmptyWhenChanged();
        }
    };

    /**
     * 检查是否为空
     */
    private void checkIfEmptyWhenChanged() {
        if (mEmptyView == null || getAdapter() == null) {
            return;
        }
        boolean emptyViewVisible = getAdapter().getItemCount() == 0;
        if (!(mEmptyView.isShown() && emptyViewVisible)) {
            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
        }
        if (!(this.isShown() && emptyViewVisible)) {
            this.setVisibility(emptyViewVisible ? GONE : VISIBLE);
        }
    }
}
