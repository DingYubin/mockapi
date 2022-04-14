package com.yubin.draw.widget.recyclerView.protocol;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * description: recyclerView的itemClick回调接口定义
 * <p>
 * date: created on 2019/2/22
 * <p>
 */
public interface ICMOnItemClickListener {

    /**
     * item click 回调的方法
     *
     * @param recyclerView The recyclerView where the click happened {@link RecyclerView}.
     * @param view         The view within the AdapterView that was clicked (this
     *                     will be a view provided by the adapter {@link View}
     * @param position     The position of the view in the adapter.
     * @param id           The row id of the item that was clicked.
     */
    void onItemClick(RecyclerView recyclerView, View view, int position, long id);
}
