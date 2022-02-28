package com.yubin.medialibrary.extra;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * description: 开思viewHolder抽象(主要差异数据的绑定)
 * <p>
 * date: created on 2019/2/22
 * <p>
 * author:   侯军(A01082)
 */
public abstract class BaseViewHolder<T extends IDiffData> extends RecyclerView.ViewHolder {

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    /**
     * 全量绑定数据
     *
     * @param data     数据
     * @param position position
     */
    public abstract void bindDataFully(@NonNull T data, int position, int count);

    /**
     * 差量绑定数据
     *
     * @param payload  差异数据
     * @param position position
     */
    public abstract void bindDiffData(@NonNull T data, @NonNull Bundle payload, int position, int count);
}
